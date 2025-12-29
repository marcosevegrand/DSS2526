package dss2526.service.producao;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;
import dss2526.service.base.BaseFacade;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProducaoFacade extends BaseFacade implements IProducaoFacade {
    private static ProducaoFacade instance;
    private ProducaoFacade() {}
    public static synchronized ProducaoFacade getInstance() {
        if (instance == null) instance = new ProducaoFacade();
        return instance;
    }

    @Override
    public void iniciarTarefa(int tarefaId) {
        Tarefa t = tarefaDAO.findById(tarefaId);
        if (t == null || t.getEstado() != EstadoTarefa.PENDENTE) return;

        // 1. Obter contexto do pedido e do restaurante
        Pedido p = pedidoDAO.findById(t.getPedidoId());
        Restaurante rest = restauranteDAO.findById(p.getRestauranteId());
        Passo passo = passoDAO.findById(t.getPassoId());

        // 2. Ajuste de Stock: Executado apenas no momento do início efetivo da tarefa
        if (rest != null && passo != null) {
            for (Integer ingId : passo.getIngredienteIds()) {
                Optional<LinhaStock> stockItem = rest.getStock().stream()
                        .filter(ls -> ls.getIngredienteId() == ingId)
                        .findFirst();
                
                if (stockItem.isPresent()) {
                    // Deduz 1 unidade base para a tarefa (simplificação da receita)
                    int novaQtd = stockItem.get().getQuantidade() - 1;
                    stockItem.get().setQuantidade(Math.max(0, novaQtd));
                }
            }
            restauranteDAO.update(rest);
        }

        // 3. Atualizar estado da tarefa
        t.setEstado(EstadoTarefa.EM_EXECUCAO);
        t.setDataInicio(LocalDateTime.now());
        tarefaDAO.update(t);
    }

    @Override
    public void refazerLinhaPedido(int pedidoId, int linhaPedidoId) {
        Pedido pedido = pedidoDAO.findById(pedidoId);
        if (pedido == null) return;

        // Localizar a linha dentro da composição do pedido
        Optional<LinhaPedido> linhaOpt = pedido.getLinhas().stream()
                .filter(l -> l.getId() == linhaPedidoId)
                .findFirst();

        if (linhaOpt.isPresent()) {
            LinhaPedido linha = linhaOpt.get();
            
            // Reverter estado do pedido para permitir re-processamento na cozinha
            pedido.setEstado(EstadoPedido.EM_PREPARACAO);
            pedido.setDataConclusao(null);
            pedidoDAO.update(pedido);

            // Gerar novas tarefas para a linha reportada com falha
            processarLinhaParaTarefas(pedidoId, linha);

            // Notificar cozinha
            Mensagem msg = new Mensagem();
            msg.setRestauranteId(pedido.getRestauranteId());
            msg.setTexto("ALERTA: Item rejeitado na entrega. Refazer: Pedido #" + pedidoId);
            msg.setDataHora(LocalDateTime.now());
            mensagemDAO.create(msg);
        }
    }

    private boolean processarLinhaParaTarefas(int pedidoId, LinhaPedido lp) {
        if (lp.getTipo() == TipoItem.PRODUTO) {
            return criarTarefasParaProduto(pedidoId, lp.getItemId(), lp.getQuantidade());
        } else {
            Menu m = menuDAO.findById(lp.getItemId());
            if (m != null) {
                boolean result = false;
                for (LinhaMenu lm : m.getLinhas()) {
                    result |= criarTarefasParaProduto(pedidoId, lm.getProdutoId(), lm.getQuantidade() * lp.getQuantidade());
                }
                return result;
            }
        }
        return false;
    }

    private boolean criarTarefasParaProduto(int pedidoId, int produtoId, int qtd) {
        Produto prod = produtoDAO.findById(produtoId);
        if (prod == null) return false;
        for (int i = 0; i < qtd; i++) {
            for (int passoId : prod.getPassoIds()) {
                Tarefa t = new Tarefa();
                t.setPedidoId(pedidoId);
                t.setProdutoId(produtoId);
                t.setPassoId(passoId);
                t.setEstado(EstadoTarefa.PENDENTE);
                t.setDataCriacao(LocalDateTime.now());
                tarefaDAO.create(t);
            }
        }
        return true;
    }

    @Override
    public void verificarNovosPedidos(int restauranteId) {
        List<Pedido> confirmados = pedidoDAO.findAllByRestaurante(restauranteId).stream()
                .filter(p -> p.getEstado() == EstadoPedido.CONFIRMADO)
                .collect(Collectors.toList());

        for (Pedido p : confirmados) {
            boolean criou = false;
            for (LinhaPedido lp : p.getLinhas()) {
                criou |= processarLinhaParaTarefas(p.getId(), lp);
            }
            if (criou) {
                p.setEstado(EstadoPedido.EM_PREPARACAO);
                pedidoDAO.update(p);
            }
        }
    }

    @Override
    public List<Tarefa> listarTarefasDisponiveisParaIniciar(int restauranteId, int estacaoId) {
        verificarNovosPedidos(restauranteId);
        Estacao est = estacaoDAO.findById(estacaoId);
        if (est == null || est instanceof Estacao.Caixa) return new ArrayList<>();

        return tarefaDAO.findAll().stream()
                .filter(t -> t.getEstado() == EstadoTarefa.PENDENTE)
                .filter(t -> {
                    Pedido p = pedidoDAO.findById(t.getPedidoId());
                    return p != null && p.getRestauranteId() == restauranteId;
                })
                .filter(t -> {
                    Passo step = passoDAO.findById(t.getPassoId());
                    return step != null && est.podeConfecionar(step.getTrabalho());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Tarefa> listarTarefasEmExecucaoNaEstacao(int restauranteId, int estacaoId) {
        return tarefaDAO.findAll().stream()
                .filter(t -> t.getEstado() == EstadoTarefa.EM_EXECUCAO || t.getEstado() == EstadoTarefa.ATRASADA)
                .filter(t -> {
                    Pedido p = pedidoDAO.findById(t.getPedidoId());
                    return p != null && p.getRestauranteId() == restauranteId;
                })
                .filter(t -> {
                    Estacao est = estacaoDAO.findById(estacaoId);
                    Passo step = passoDAO.findById(t.getPassoId());
                    return est != null && step != null && est.podeConfecionar(step.getTrabalho());
                })
                .collect(Collectors.toList());
    }

    @Override
    public void concluirTarefa(int tarefaId) {
        Tarefa t = tarefaDAO.findById(tarefaId);
        if (t == null) return;
        t.setEstado(EstadoTarefa.CONCLUIDA);
        t.setDataConclusao(LocalDateTime.now());
        tarefaDAO.update(t);

        List<Tarefa> total = tarefaDAO.findAllByPedido(t.getPedidoId());
        if (total.stream().allMatch(x -> x.getEstado() == EstadoTarefa.CONCLUIDA)) {
            Pedido p = pedidoDAO.findById(t.getPedidoId());
            p.setEstado(EstadoPedido.PRONTO);
            pedidoDAO.update(p);
        }
    }

    @Override
    public void processarPagamento(int pedidoId) {
        Pedido p = pedidoDAO.findById(pedidoId);
        Pagamento pag = pagamentoDAO.findByPedido(pedidoId);
        if (p != null && pag != null) {
            pag.setConfirmado(true);
            pag.setData(LocalDateTime.now());
            pagamentoDAO.update(pag);
            p.setEstado(EstadoPedido.CONFIRMADO);
            pedidoDAO.update(p);
            verificarNovosPedidos(p.getRestauranteId());
        }
    }

    @Override
    public List<Pedido> listarPedidosAguardandoPagamento(int restauranteId) {
        return pedidoDAO.findAllByRestaurante(restauranteId).stream()
                .filter(p -> p.getEstado() == EstadoPedido.AGUARDA_PAGAMENTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Pedido> listarPedidosProntosParaEntrega(int restauranteId) {
        return pedidoDAO.findAllByRestaurante(restauranteId).stream()
                .filter(p -> p.getEstado() == EstadoPedido.PRONTO)
                .collect(Collectors.toList());
    }

    @Override
    public void confirmarEntrega(int pedidoId) {
        Pedido p = pedidoDAO.findById(pedidoId);
        if (p != null) {
            p.setEstado(EstadoPedido.ENTREGUE);
            p.setDataConclusao(LocalDateTime.now());
            pedidoDAO.update(p);
        }
    }

    @Override public void cancelarPedido(int pId) { 
        Pedido p = pedidoDAO.findById(pId);
        if (p != null) { p.setEstado(EstadoPedido.CANCELADO); pedidoDAO.update(p); }
    }
    @Override public void registarAtrasoTarefa(int tId) {
        Tarefa t = tarefaDAO.findById(tId);
        if (t != null) { t.setEstado(EstadoTarefa.ATRASADA); tarefaDAO.update(t); }
    }
    @Override public List<Pedido> consultarMonitorGlobal(int rId) { 
        return pedidoDAO.findAllByRestaurante(rId).stream()
                .filter(p -> p.getEstado() != EstadoPedido.ENTREGUE && p.getEstado() != EstadoPedido.CANCELADO)
                .collect(Collectors.toList()); 
    }
    @Override public List<Mensagem> consultarMensagensRestaurante(int rId) { return mensagemDAO.findAllByRestaurante(rId); }
}
package dss2526.service.producao;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;
import dss2526.service.base.BaseFacade;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class ProducaoFacade extends BaseFacade implements IProducaoFacade {
    private static ProducaoFacade instance;
    private ProducaoFacade() {}
    public static synchronized ProducaoFacade getInstance() {
        if (instance == null) instance = new ProducaoFacade();
        return instance;
    }

    @Override
    public List<Tarefa> listarTarefasParaIniciar(int rId, int eId) {
        verificarNovosPedidos(rId);
        Estacao est = estacaoDAO.findById(eId);
        
        return tarefaDAO.findAll().stream()
            .filter(t -> t.getEstado() == EstadoTarefa.PENDENTE)
            .filter(t -> {
                Pedido p = pedidoDAO.findById(t.getPedidoId());
                return p != null && p.getRestauranteId() == rId;
            })
            .filter(t -> est.podeConfecionar(passoDAO.findById(t.getPassoId()).getTrabalho()))
            .filter(t -> isTarefaSincronizada(t))
            .collect(Collectors.toList());
    }

    @Override
    public List<Tarefa> listarTarefasEmExecucao(int estacaoId) {
        return tarefaDAO.findAll().stream()
                .filter(t -> t.getEstado() == EstadoTarefa.EM_EXECUCAO || t.getEstado() == EstadoTarefa.ATRASADA)
                .filter(t -> t.getEstacaoId() == estacaoId)
                .collect(Collectors.toList());
    }

    private boolean isTarefaSincronizada(Tarefa t) {
        List<Tarefa> todas = tarefaDAO.findAllByPedido(t.getPedidoId());
        
        long maxDuracaoTotal = todas.stream()
                .mapToLong(x -> passoDAO.findById(x.getPassoId()).getDuracao().toMinutes())
                .max().orElse(0);
        
        long minhaDuracao = passoDAO.findById(t.getPassoId()).getDuracao().toMinutes();

        Optional<Tarefa> longaEmExecucao = todas.stream()
                .filter(x -> x.getEstado() == EstadoTarefa.EM_EXECUCAO || x.getEstado() == EstadoTarefa.ATRASADA)
                .filter(x -> passoDAO.findById(x.getPassoId()).getDuracao().toMinutes() == maxDuracaoTotal)
                .findFirst();

        if (longaEmExecucao.isEmpty()) {
            return minhaDuracao == maxDuracaoTotal || todas.stream().noneMatch(x -> x.getEstado() == EstadoTarefa.EM_EXECUCAO);
        } else {
            long decorrido = Duration.between(longaEmExecucao.get().getDataInicio(), LocalDateTime.now()).toMinutes();
            long faltaParaLonga = Math.max(0, maxDuracaoTotal - decorrido);
            return minhaDuracao >= faltaParaLonga;
        }
    }

    private void verificarNovosPedidos(int rId) {
        pedidoDAO.findAllByRestaurante(rId).stream()
            .filter(p -> p.getEstado() == EstadoPedido.CONFIRMADO)
            .forEach(p -> {
                p.getLinhas().forEach(l -> gerarTarefas(p.getId(), l));
                p.setEstado(EstadoPedido.EM_PREPARACAO);
                pedidoDAO.update(p);
            });
    }

    private void gerarTarefas(int pId, LinhaPedido lp) {
        if (lp.getTipo() == TipoItem.PRODUTO) {
            Produto pr = produtoDAO.findById(lp.getItemId());
            if (pr != null) {
                for (int i=0; i<lp.getQuantidade(); i++) {
                    pr.getPassoIds().forEach(sid -> {
                        Tarefa t = new Tarefa();
                        t.setPedidoId(pId); t.setProdutoId(pr.getId()); t.setPassoId(sid);
                        t.setEstado(EstadoTarefa.PENDENTE); t.setDataCriacao(LocalDateTime.now());
                        tarefaDAO.create(t);
                    });
                }
            }
        } else {
            Menu m = menuDAO.findById(lp.getItemId());
            if (m != null) {
                m.getLinhas().forEach(lm -> {
                    LinhaPedido d = new LinhaPedido(); d.setItemId(lm.getProdutoId()); d.setTipo(TipoItem.PRODUTO);
                    d.setQuantidade(lm.getQuantidade() * lp.getQuantidade()); gerarTarefas(pId, d);
                });
            }
        }
    }

    @Override
    public void iniciarTarefa(int tId, int estacaoId) { 
        Tarefa t = tarefaDAO.findById(tId); 
        if (t != null) {
            t.setEstado(EstadoTarefa.EM_EXECUCAO); 
            t.setEstacaoId(estacaoId); 
            t.setDataInicio(LocalDateTime.now()); 
            tarefaDAO.update(t); 
        }
    }

    @Override
    public void concluirTarefa(int tId) {
        Tarefa t = tarefaDAO.findById(tId);
        if (t == null) return;

        t.setEstado(EstadoTarefa.CONCLUIDA);
        t.setDataConclusao(LocalDateTime.now());
        tarefaDAO.update(t);
        
        List<Tarefa> todas = tarefaDAO.findAllByPedido(t.getPedidoId());
        if (todas.stream().allMatch(x -> x.getEstado() == EstadoTarefa.CONCLUIDA)) {
            Pedido p = pedidoDAO.findById(t.getPedidoId());
            p.setEstado(EstadoPedido.PRONTO);
            pedidoDAO.update(p);
        }
    }

    @Override
    public List<Ingrediente> listarIngredientesDaTarefa(int tarefaId) {
        Tarefa t = tarefaDAO.findById(tarefaId);
        if (t == null) return new ArrayList<>();
        
        Passo passo = passoDAO.findById(t.getPassoId());
        if (passo != null && !passo.getIngredienteIds().isEmpty()) {
            return passo.getIngredienteIds().stream()
                    .map(ingredienteDAO::findById).filter(Objects::nonNull).collect(Collectors.toList());
        }
        Produto p = produtoDAO.findById(t.getProdutoId());
        if (p != null) {
            return p.getLinhas().stream()
                    .map(lp -> ingredienteDAO.findById(lp.getIngredienteId())).filter(Objects::nonNull)
                    .distinct().collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public void atrasarTarefa(int tId, int iId) {
        Tarefa t = tarefaDAO.findById(tId);
        if (t == null) return;

        t.setEstado(EstadoTarefa.ATRASADA);
        tarefaDAO.update(t);
        
        Ingrediente ing = ingredienteDAO.findById(iId);
        Pedido ped = pedidoDAO.findById(t.getPedidoId());
        
        Mensagem m = new Mensagem();
        m.setRestauranteId(ped.getRestauranteId());
        m.setTexto("ALERTA COZINHA: Falta de " + (ing != null ? ing.getNome() : "Ingrediente ID " + iId) + 
                   " para o Pedido #" + ped.getId() + " (Tarefa #" + tId + ")");
        m.setDataHora(LocalDateTime.now());
        mensagemDAO.create(m);
    }

    @Override
    public Duration processarPagamentoCaixa(int pId) {
        Pagamento pag = pagamentoDAO.findByPedido(pId);
        Pedido p = pedidoDAO.findById(pId);
        
        if (pag != null && p != null) {
            // 1. Confirma Pagamento
            pag.setConfirmado(true);
            pag.setData(LocalDateTime.now());
            pagamentoDAO.update(pag);
            
            // 2. Confirma Pedido
            p.setEstado(EstadoPedido.CONFIRMADO);
            pedidoDAO.update(p);
            
            // 3. Gera Tarefas (O método verificarNovosPedidos trata dos pedidos confirmados)
            verificarNovosPedidos(p.getRestauranteId());
            
            // 4. Calcula Estimativa (Máximo passo + 5 min)
            List<Tarefa> tarefasDoPedido = tarefaDAO.findAllByPedido(pId);
            
            long maxMinutos = tarefasDoPedido.stream()
                .mapToLong(t -> {
                    Passo passo = passoDAO.findById(t.getPassoId());
                    return passo != null ? passo.getDuracao().toMinutes() : 0;
                })
                .max()
                .orElse(0); // Se não houver tarefas (ex: só água), tempo de confecção é 0
            
            return Duration.ofMinutes(maxMinutos + 5);
        }
        return Duration.ZERO;
    }

    @Override
    public void confirmarEntrega(int pId) {
        Pedido p = pedidoDAO.findById(pId);
        if (p != null) {
            p.setEstado(EstadoPedido.ENTREGUE);
            p.setDataConclusao(LocalDateTime.now());
            pedidoDAO.update(p);
        }
    }

    @Override
    public void solicitarRefacaoItens(int pId, List<Integer> lIds) {
        Pedido p = pedidoDAO.findById(pId);
        if (p != null) {
            p.getLinhas().stream().filter(l -> lIds.contains(l.getId())).forEach(l -> gerarTarefas(pId, l));
            p.setEstado(EstadoPedido.EM_PREPARACAO); 
            p.setDataConclusao(null); 
            pedidoDAO.update(p);
            
            Mensagem m = new Mensagem();
            m.setRestauranteId(p.getRestauranteId());
            m.setTexto("DEVOLUÇÃO: Itens do Pedido #" + pId + " devolvidos para refazer.");
            m.setDataHora(LocalDateTime.now());
            mensagemDAO.create(m);
        }
    }
    
    @Override public List<Pedido> listarAguardaPagamento(int rId) { 
        return pedidoDAO.findAllByRestaurante(rId).stream().filter(p -> p.getEstado() == EstadoPedido.AGUARDA_PAGAMENTO).collect(Collectors.toList()); 
    }
    @Override public List<Pedido> listarProntos(int rId) { 
        return pedidoDAO.findAllByRestaurante(rId).stream().filter(p -> p.getEstado() == EstadoPedido.PRONTO).collect(Collectors.toList()); 
    }
    @Override public Map<Pedido, String> obterProgressoMonitor(int rId) {
        return pedidoDAO.findAllByRestaurante(rId).stream()
            .filter(p -> p.getEstado() == EstadoPedido.CONFIRMADO || p.getEstado() == EstadoPedido.EM_PREPARACAO || p.getEstado() == EstadoPedido.PRONTO)
            .collect(Collectors.toMap(p -> p, p -> {
                List<Tarefa> tfs = tarefaDAO.findAllByPedido(p.getId());
                long concluidas = tfs.stream().filter(t -> t.getEstado() == EstadoTarefa.CONCLUIDA).count();
                if (tfs.isEmpty()) return "Aguardando Início";
                return concluidas + "/" + tfs.size() + " tarefas";
            }));
    }
    @Override public List<Mensagem> listarMensagens(int rId) {
        return mensagemDAO.findAllByRestaurante(rId).stream().sorted(Comparator.comparing(Mensagem::getDataHora).reversed()).collect(Collectors.toList());
    }
}
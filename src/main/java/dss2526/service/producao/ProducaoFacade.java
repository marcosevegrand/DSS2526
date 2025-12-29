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
    public List<Tarefa> listarTarefasSincronizadas(int rId, int eId) {
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
            return minhaDuracao == maxDuracaoTotal;
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
            for (int i=0; i<lp.getQuantidade(); i++) {
                pr.getPassoIds().forEach(sid -> {
                    Tarefa t = new Tarefa();
                    t.setPedidoId(pId); t.setProdutoId(pr.getId()); t.setPassoId(sid);
                    t.setEstado(EstadoTarefa.PENDENTE); t.setDataCriacao(LocalDateTime.now());
                    tarefaDAO.create(t);
                });
            }
        } else {
            Menu m = menuDAO.findById(lp.getItemId());
            m.getLinhas().forEach(lm -> {
                LinhaPedido d = new LinhaPedido(); d.setItemId(lm.getProdutoId()); d.setTipo(TipoItem.PRODUTO);
                d.setQuantidade(lm.getQuantidade() * lp.getQuantidade()); gerarTarefas(pId, d);
            });
        }
    }

    @Override
    public void concluirTarefa(int tId) {
        Tarefa t = tarefaDAO.findById(tId);
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
    public void atrasarTarefa(int tId, int iId) {
        Tarefa t = tarefaDAO.findById(tId);
        t.setEstado(EstadoTarefa.ATRASADA);
        tarefaDAO.update(t);
        
        Ingrediente ing = ingredienteDAO.findById(iId);
        Pedido ped = pedidoDAO.findById(t.getPedidoId());
        Mensagem m = new Mensagem();
        m.setRestauranteId(ped.getRestauranteId());
        m.setTexto("ATRASO CRÍTICO: Ingrediente " + ing.getNome() + " em falta (Tarefa #" + tId + ")");
        m.setDataHora(LocalDateTime.now());
        mensagemDAO.create(m);
    }

    @Override
    public void processarPagamentoCaixa(int pId) {
        Pagamento pag = pagamentoDAO.findByPedido(pId);
        if (pag != null) {
            pag.setConfirmado(true);
            pag.setData(LocalDateTime.now());
            pagamentoDAO.update(pag);
            Pedido p = pedidoDAO.findById(pId);
            p.setEstado(EstadoPedido.CONFIRMADO);
            pedidoDAO.update(p);
            verificarNovosPedidos(p.getRestauranteId());
        }
    }

    @Override
    public void confirmarEntrega(int pId) {
        Pedido p = pedidoDAO.findById(pId);
        p.setEstado(EstadoPedido.ENTREGUE);
        p.setDataConclusao(LocalDateTime.now());
        pedidoDAO.update(p);
    }

    @Override
    public void solicitarRefacaoItens(int pId, List<Integer> lIds) {
        Pedido p = pedidoDAO.findById(pId);
        p.getLinhas().stream().filter(l -> lIds.contains(l.getId())).forEach(l -> gerarTarefas(pId, l));
        p.setEstado(EstadoPedido.EM_PREPARACAO);
        p.setDataConclusao(null);
        pedidoDAO.update(p);
    }

    @Override public void iniciarTarefa(int tId) { Tarefa t = tarefaDAO.findById(tId); t.setEstado(EstadoTarefa.EM_EXECUCAO); t.setDataInicio(LocalDateTime.now()); tarefaDAO.update(t); }
    @Override public List<Pedido> listarAguardaPagamento(int rId) { return pedidoDAO.findAllByRestaurante(rId).stream().filter(p -> p.getEstado() == EstadoPedido.AGUARDA_PAGAMENTO).collect(Collectors.toList()); }
    @Override public List<Pedido> listarProntos(int rId) { return pedidoDAO.findAllByRestaurante(rId).stream().filter(p -> p.getEstado() == EstadoPedido.PRONTO).collect(Collectors.toList()); }
    @Override public Map<Pedido, Long> obterProgressoMonitor(int rId) {
        return pedidoDAO.findAllByRestaurante(rId).stream()
            .filter(p -> p.getEstado() == EstadoPedido.EM_PREPARACAO || p.getEstado() == EstadoPedido.PRONTO)
            .collect(Collectors.toMap(p -> p, p -> tarefaDAO.findAllByPedido(p.getId()).stream().filter(t -> t.getEstado() == EstadoTarefa.CONCLUIDA).count()));
    }
}
package dss2526.ui.controller;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.TipoItem;
import dss2526.service.producao.*;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ProducaoController {
    private final IProducaoFacade facade = ProducaoFacade.getInstance();
    private int rId, eId;
    private List<Integer> cacheTaskIdsPendentes = new ArrayList<>();
    private List<Integer> cacheTaskIdsEmExecucao = new ArrayList<>();
    private List<Integer> cacheOrderIds = new ArrayList<>();
    private List<Integer> cacheIngIds = new ArrayList<>();
    private List<Restaurante> cacheRestaurantes = new ArrayList<>();
    private List<Estacao> cacheEstacoes = new ArrayList<>();

    public List<String> getRestaurantes() { cacheRestaurantes = facade.listarRestaurantes(); return cacheRestaurantes.stream().map(Restaurante::getNome).collect(Collectors.toList()); }
    public void selecionarRestaurante(int i) { this.rId = cacheRestaurantes.get(i).getId(); }
    public List<String> getEstacoes() { cacheEstacoes = facade.listarEstacoesDeRestaurante(rId); return cacheEstacoes.stream().map(Estacao::getNome).collect(Collectors.toList()); }
    public void selecionarEstacao(int i) { this.eId = cacheEstacoes.get(i).getId(); }
    public String getNomeEstacao() { return facade.obterEstacao(eId).getNome(); }
    public boolean isCaixa() { return facade.obterEstacao(eId) instanceof Estacao.Caixa; }

    public List<String> getTarefasPendentes() {
        List<Tarefa> ts = facade.listarTarefasParaIniciar(rId, eId);
        this.cacheTaskIdsPendentes = ts.stream().map(Tarefa::getId).collect(Collectors.toList());
        return ts.stream().map(t -> "Pedido #" + t.getPedidoId() + " : " + facade.obterPasso(t.getPassoId()).getNome()).collect(Collectors.toList());
    }
    public void iniciarTarefaPendente(int uiIndex) { facade.iniciarTarefa(cacheTaskIdsPendentes.get(uiIndex), this.eId); }

    public List<String> getTarefasEmExecucao() {
        List<Tarefa> ts = facade.listarTarefasEmExecucao(eId);
        this.cacheTaskIdsEmExecucao = ts.stream().map(Tarefa::getId).collect(Collectors.toList());
        return ts.stream().map(t -> "Pedido #" + t.getPedidoId() + " : " + facade.obterPasso(t.getPassoId()).getNome() + " [" + t.getEstado() + "]").collect(Collectors.toList());
    }
    public void concluirTarefaEmExecucao(int uiIndex) { facade.concluirTarefa(cacheTaskIdsEmExecucao.get(uiIndex)); }

    public List<String> getIngredientesDaTarefaEmExecucao(int uiIndex) {
        int tId = cacheTaskIdsEmExecucao.get(uiIndex);
        List<Ingrediente> l = facade.listarIngredientesDaTarefa(tId);
        this.cacheIngIds = l.stream().map(Ingrediente::getId).collect(Collectors.toList());
        return l.stream().map(Ingrediente::getNome).collect(Collectors.toList());
    }
    public void atrasarTarefaEmExecucao(int taskUiIndex, int ingUiIndex) { facade.atrasarTarefa(cacheTaskIdsEmExecucao.get(taskUiIndex), cacheIngIds.get(ingUiIndex)); }

    public List<String> getPedidosNaoPagos() {
        List<Pedido> p = facade.listarAguardaPagamento(rId);
        this.cacheOrderIds = p.stream().map(Pedido::getId).collect(Collectors.toList());
        return p.stream().map(x -> "Pedido #" + x.getId() + " | Total: " + x.calcularPrecoTotal() + "€").collect(Collectors.toList());
    }
    public String confirmarPagamento(int i) { Duration estimativa = facade.processarPagamentoCaixa(cacheOrderIds.get(i)); return "Pagamento confirmado. Tempo estimado de espera: " + estimativa.toMinutes() + " minutos."; }

    public List<String> getPedidosProntos() {
        List<Pedido> p = facade.listarProntos(rId);
        this.cacheOrderIds = p.stream().map(Pedido::getId).collect(Collectors.toList());
        return p.stream().map(x -> "Pedido #" + x.getId() + " (Pronto para entrega)").collect(Collectors.toList());
    }
    public void entregarPedido(int i) { facade.confirmarEntrega(cacheOrderIds.get(i)); }

    public List<String> getLinhasPedido(int i) {
        return facade.obterPedido(cacheOrderIds.get(i)).getLinhas().stream()
                .map(l -> { String item = (l.getTipo() == TipoItem.PRODUTO) ? facade.obterProduto(l.getItemId()).getNome() : facade.obterMenu(l.getItemId()).getNome(); return item + " (Qtd: " + l.getQuantidade() + ")"; })
                .collect(Collectors.toList());
    }
    public void solicitarRefacao(int pIdx, List<Integer> lIdxs) {
        Pedido p = facade.obterPedido(cacheOrderIds.get(pIdx));
        List<Integer> lines = lIdxs.stream().filter(idx -> idx >= 0 && idx < p.getLinhas().size()).map(idx -> p.getLinhas().get(idx).getId()).collect(Collectors.toList());
        facade.solicitarRefacaoItens(p.getId(), lines);
    }

    public List<String> getMonitorGlobal() {
        return facade.obterProgressoMonitor(rId).entrySet().stream().sorted(Comparator.comparingInt(e -> e.getKey().getId()))
                .map(e -> String.format("Pedido #%d [%-12s] -> %s", e.getKey().getId(), e.getKey().getEstado(), e.getValue())).collect(Collectors.toList());
    }

    public List<String> getMensagens() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        return facade.listarMensagens(rId).stream().map(m -> "[" + m.getDataHora().format(dtf) + "] " + m.getTexto()).collect(Collectors.toList());
    }
}
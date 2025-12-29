package dss2526.ui.controller;

import dss2526.domain.entity.*;
import dss2526.service.producao.*;
import java.util.*;
import java.util.stream.Collectors;

public class ProducaoController {
    private final IProducaoFacade facade = ProducaoFacade.getInstance();
    private int rId, eId;
    private List<Integer> cacheTaskIds = new ArrayList<>();
    private List<Integer> cacheOrderIds = new ArrayList<>();
    private List<Integer> cacheIngIds = new ArrayList<>();
    private List<Restaurante> cacheRestaurantes = new ArrayList<>();
    private List<Estacao> cacheEstacoes = new ArrayList<>();

    public List<String> getRestaurantes() { 
        cacheRestaurantes = facade.listarRestaurantes();
        return cacheRestaurantes.stream().map(r -> r.getNome()).collect(Collectors.toList()); 
    }
    public void selecionarRestaurante(int i) { this.rId = cacheRestaurantes.get(i).getId(); }

    public List<String> getEstacoes() { 
        cacheEstacoes = facade.listarEstacoesDeRestaurante(rId);
        return cacheEstacoes.stream().map(e -> e.getNome()).collect(Collectors.toList()); 
    }
    public void selecionarEstacao(int i) { this.eId = cacheEstacoes.get(i).getId(); }
    public String getNomeEstacao() { return facade.obterEstacao(eId).getNome(); }
    
    public boolean isCaixa() { return facade.obterEstacao(eId) instanceof Estacao.Caixa; }

    public List<String> getTarefasDisponiveis() {
        List<Tarefa> ts = facade.listarTarefasSincronizadas(rId, eId);
        this.cacheTaskIds = ts.stream().map(t -> (Integer) t.getId()).collect(Collectors.toList());
        return ts.stream()
                 .map(t -> "Pedido " + t.getPedidoId() + " -> " + facade.obterPasso(t.getPassoId()).getNome())
                 .collect(Collectors.toList());
    }
    public void iniciarTarefa(int i) { facade.iniciarTarefa(cacheTaskIds.get(i)); }
    public void concluirTarefa(int i) { facade.concluirTarefa(cacheTaskIds.get(i)); }
    
    public List<String> getIngredientes() { 
        List<Ingrediente> l = facade.listarIngredientes();
        this.cacheIngIds = l.stream().map(ing -> (Integer) ing.getId()).collect(Collectors.toList());
        return l.stream().map(ing -> ing.getNome()).collect(Collectors.toList());
    }
    public void atrasarTarefa(int tIdx, int ingIdx) { facade.atrasarTarefa(cacheTaskIds.get(tIdx), cacheIngIds.get(ingIdx)); }

    public List<String> getPedidosNaoPagos() {
        List<Pedido> p = facade.listarAguardaPagamento(rId);
        this.cacheOrderIds = p.stream().map(ped -> (Integer) ped.getId()).collect(Collectors.toList());
        return p.stream().map(x -> "Pedido #" + x.getId() + " (" + x.calcularPrecoTotal() + "€)").collect(Collectors.toList());
    }
    public void confirmarPagamento(int i) { facade.processarPagamentoCaixa(cacheOrderIds.get(i)); }
    
    public List<String> getPedidosProntos() {
        List<Pedido> p = facade.listarProntos(rId);
        this.cacheOrderIds = p.stream().map(ped -> (Integer) ped.getId()).collect(Collectors.toList());
        return p.stream().map(x -> "Pedido #" + x.getId()).collect(Collectors.toList());
    }
    public void entregarPedido(int i) { facade.confirmarEntrega(cacheOrderIds.get(i)); }
    
    public List<String> getLinhasPedido(int i) { 
        return facade.obterPedido(cacheOrderIds.get(i)).getLinhas().stream()
            .map(l -> l.getItemId() + " (qtd " + l.getQuantidade() + ")")
            .collect(Collectors.toList()); 
    }
    public void solicitarRefacao(int pIdx, List<Integer> lIdxs) {
        Pedido p = facade.obterPedido(cacheOrderIds.get(pIdx));
        List<Integer> lines = lIdxs.stream().map(idx -> p.getLinhas().get(idx).getId()).collect(Collectors.toList());
        facade.solicitarRefacaoItens(p.getId(), lines);
    }
    
    public List<String> getMonitorGlobal() { 
        return facade.obterProgressoMonitor(rId).entrySet().stream()
            .map(e -> "Pedido #" + e.getKey().getId() + " [" + e.getKey().getEstado() + "] | Passos Concluídos: " + e.getValue())
            .collect(Collectors.toList()); 
    }
}
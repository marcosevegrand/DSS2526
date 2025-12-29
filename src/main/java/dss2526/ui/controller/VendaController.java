package dss2526.ui.controller;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;
import dss2526.domain.contract.Item;
import dss2526.service.venda.*;
import java.util.*;
import java.util.stream.Collectors;

public class VendaController {
    private final IVendaFacade facade = VendaFacade.getInstance();
    private int rId, pId;
    private List<Integer> excludeAlergs = new ArrayList<>();
    private List<Integer> cacheItemIds = new ArrayList<>();
    private List<Restaurante> cacheRests = new ArrayList<>();
    private List<Ingrediente> cacheAlergs = new ArrayList<>();

    public List<String> getRestaurantes() { 
        cacheRests = facade.listarRestaurantes();
        return cacheRests.stream().map(Restaurante::getNome).collect(Collectors.toList()); 
    }
    public void setRestaurante(int idx) { this.rId = cacheRests.get(idx).getId(); }

    public List<String> getAlergenicos() { 
        cacheAlergs = facade.listarAlergenicosDisponiveis();
        return cacheAlergs.stream().map(Ingrediente::getAlergenico).collect(Collectors.toList()); 
    }
    public void definirExclusoes(List<Integer> idxs) {
        this.excludeAlergs = idxs.stream().map(i -> cacheAlergs.get(i).getId()).collect(Collectors.toList());
    }

    public void iniciarNovoPedido() { this.pId = facade.iniciarPedido(rId).getId(); }
    
    public List<String> getCatalogo() {
        List<Item> its = facade.listarCatalogoFiltrado(rId, excludeAlergs);
        cacheItemIds = its.stream().map(Item::getId).collect(Collectors.toList());
        return its.stream().map(i -> i.getNome() + " (" + i.getPreco() + "€)").collect(Collectors.toList());
    }
    
    public void adicionarItem(int i, int q) { 
        facade.adicionarItemAoPedido(pId, cacheItemIds.get(i), TipoItem.PRODUTO, q, ""); 
    }
    
    public List<String> getCarrinho() { 
        Pedido p = facade.obterPedido(pId);
        return p.getLinhas().stream()
            .map(l -> l.getQuantidade() + "x Item " + l.getItemId() + " (" + l.getPrecoUnitario() + "€)")
            .collect(Collectors.toList()); 
    }
    
    public void removerItem(int idx) { 
        List<LinhaPedido> linhas = facade.obterPedido(pId).getLinhas();
        if(idx >= 0 && idx < linhas.size()) facade.removerItemDoPedido(pId, linhas.get(idx).getId()); 
    }
    
    public String getResumo() { 
        Pedido p = facade.obterPedido(pId); 
        return "TOTAL A PAGAR: " + p.calcularPrecoTotal() + "€ | Artigos: " + p.getLinhas().size(); 
    }
    
    public void pagar(int opt) { facade.processarPagamento(pId, opt == 1 ? TipoPagamento.TERMINAL : TipoPagamento.CAIXA); }
    public void cancelar() { facade.cancelarPedido(pId); }
    
    public List<String> getEstadoPedidos() { 
        return facade.listarPedidosAtivos(rId).stream()
            .map(p -> "Pedido #" + p.getId() + ": " + p.getEstado())
            .collect(Collectors.toList()); 
    }
}
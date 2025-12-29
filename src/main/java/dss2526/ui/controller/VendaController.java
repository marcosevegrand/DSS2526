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
    private List<Integer> excludeAlergsIds = new ArrayList<>();
    
    private List<Restaurante> cacheRests = new ArrayList<>();
    private List<Item> cacheItems = new ArrayList<>();

    public List<String> getRestaurantes() { 
        cacheRests = facade.listarRestaurantes();
        return cacheRests.stream().map(Restaurante::getNome).collect(Collectors.toList()); 
    }
    
    public void selecionarRestaurante(int idx) { 
        if (idx >= 0 && idx < cacheRests.size()) this.rId = cacheRests.get(idx).getId(); 
    }

    public void iniciarNovoPedido() { 
        this.pId = facade.iniciarPedido(rId).getId(); 
        this.excludeAlergsIds.clear();
    }

    public List<String> getListaAlergenicos() {
        return facade.listarAlergenicosDisponiveis().stream()
                .map(Ingrediente::getAlergenico)
                .filter(Objects::nonNull)
                .map(String::trim)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public void definirExclusoes(List<String> nomesSelecionados) {
        // Normalização agressiva para garantir que "GLUTEN" casa com "Gluten" ou " GLUTEN "
        Set<String> normalizados = nomesSelecionados.stream()
                .map(s -> s.trim().toUpperCase())
                .collect(Collectors.toSet());

        this.excludeAlergsIds = facade.listarAlergenicosDisponiveis().stream()
                .filter(ing -> ing.getAlergenico() != null && 
                               normalizados.contains(ing.getAlergenico().trim().toUpperCase()))
                .map(Ingrediente::getId)
                .collect(Collectors.toList());
    }

    public List<String> getCatalogo() {
        cacheItems = facade.listarCatalogoFiltrado(rId, excludeAlergsIds);
        return cacheItems.stream()
                .map(i -> i.getNome() + " - " + String.format("%.2f", i.getPreco()) + "€")
                .collect(Collectors.toList());
    }

    public boolean adicionarItem(int idx, int qtd, String obs) {
        if (idx < 0 || idx >= cacheItems.size()) return false;
        Item item = cacheItems.get(idx);
        TipoItem tipo = (item instanceof Produto) ? TipoItem.PRODUTO : TipoItem.MENU;
        facade.adicionarItemAoPedido(pId, item.getId(), tipo, qtd, obs);
        return true;
    }

    public List<String> getLinhasCarrinho() {
        Pedido p = facade.obterPedido(pId);
        if (p == null) return Collections.emptyList();
        return p.getLinhas().stream()
                .map(l -> String.format("%dx %s%s", l.getQuantidade(), 
                        facade.obterNomeItem(l.getItemId(), l.getTipo()), 
                        l.getObservacao().isEmpty() ? "" : " [" + l.getObservacao() + "]"))
                .collect(Collectors.toList());
    }

    public void removerItem(int idx, int qtd) {
        List<LinhaPedido> linhas = facade.obterPedido(pId).getLinhas();
        if (idx >= 0 && idx < linhas.size()) facade.removerQuantidadeDoPedido(pId, linhas.get(idx).getId(), qtd);
    }

    public String getResumoPedido() {
        Pedido p = facade.obterPedido(pId);
        return String.format("TOTAL: %.2f€ (%d itens no pedido)", p.calcularPrecoTotal(), p.getLinhas().size());
    }

    public void pagar(int opt) { facade.processarPagamento(pId, opt == 1 ? TipoPagamento.TERMINAL : TipoPagamento.CAIXA); }
    public void cancelar() { facade.cancelarPedido(pId); }
    public List<String> getEcraPedidosAtivos() {
        return facade.listarPedidosAtivos(rId).stream()
                .map(p -> "Pedido #" + p.getId() + " - " + p.getEstado())
                .collect(Collectors.toList());
    }
}
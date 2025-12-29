package dss2526.ui.controller;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;
import dss2526.domain.contract.Item;
import dss2526.service.venda.IVendaFacade;
import dss2526.service.venda.VendaFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VendaController {

    private final IVendaFacade facade;
    private int restauranteId = -1;
    private int pedidoId = -1;
    private List<Integer> alergenicosSelecionadosIds = new ArrayList<>();
    
    // Caches para mapeamento de UI
    private List<Integer> cacheItemIds = new ArrayList<>();
    private List<TipoItem> cacheItemTipos = new ArrayList<>();
    private List<Integer> cacheAlergenicosIds = new ArrayList<>();
    private List<Integer> cacheLinhaIds = new ArrayList<>();

    public VendaController() {
        this.facade = VendaFacade.getInstance();
    }

    public List<String> getNomesRestaurantes() {
        List<Restaurante> rests = facade.listarRestaurantes();
        return rests.stream().map(r -> r.getNome() + " [" + r.getLocalizacao() + "]").collect(Collectors.toList());
    }

    public void selecionarRestaurante(int index) {
        this.restauranteId = facade.listarRestaurantes().get(index).getId();
    }

    public List<String> getNomesAlergenicos() {
        List<Ingrediente> lista = facade.listarAlergenicosDisponiveis();
        this.cacheAlergenicosIds = lista.stream().map(Ingrediente::getId).collect(Collectors.toList());
        return lista.stream().map(Ingrediente::getAlergenico).collect(Collectors.toList());
    }

    public void setAlergenicosPorIndices(List<Integer> indices) {
        this.alergenicosSelecionadosIds = indices.stream()
                .map(i -> cacheAlergenicosIds.get(i))
                .collect(Collectors.toList());
    }

    public void iniciarPedido() {
        Pedido p = facade.iniciarPedido(restauranteId);
        this.pedidoId = p.getId();
    }

    public List<String> getCatalogoFormatado() {
        List<Item> itens = facade.listarItemsDisponiveis(restauranteId, alergenicosSelecionadosIds);
        this.cacheItemIds = new ArrayList<>();
        this.cacheItemTipos = new ArrayList<>();
        
        List<String> display = new ArrayList<>();
        for (Item it : itens) {
            cacheItemIds.add(it.getId());
            TipoItem tipo = (it instanceof Produto) ? TipoItem.PRODUTO : TipoItem.MENU;
            cacheItemTipos.add(tipo);
            String label = (tipo == TipoItem.PRODUTO) ? "[PROD]" : "[MENU]";
            display.add(String.format("%-7s %-30s | %.2f EUR", label, it.getNome(), it.getPreco()));
        }
        return display;
    }

    public void adicionarItem(int index, int qtd, String obs) {
        facade.adicionarLinhaAoPedido(pedidoId, cacheItemIds.get(index), cacheItemTipos.get(index), qtd, obs);
    }

    public List<String> getItensNoPedido() {
        Pedido p = facade.obterPedido(pedidoId);
        this.cacheLinhaIds = new ArrayList<>();
        List<String> display = new ArrayList<>();
        for (LinhaPedido lp : p.getLinhas()) {
            cacheLinhaIds.add(lp.getId());
            String nome = (lp.getTipo() == TipoItem.PRODUTO) ? 
                facade.obterProduto(lp.getItemId()).getNome() : facade.obterMenu(lp.getItemId()).getNome();
            display.add(nome + " (x" + lp.getQuantidade() + ") - " + lp.getPreco() + " EUR");
        }
        return display;
    }

    public void removerItem(int index) {
        int linhaId = cacheLinhaIds.get(index);
        facade.removerLinhaDoPedido(pedidoId, linhaId);
    }

    public void cancelarPedidoAtual() {
        if (pedidoId != -1) facade.cancelarPedidoVenda(pedidoId);
    }

    public String getResumoPedido() {
        Pedido p = facade.obterPedido(pedidoId);
        StringBuilder sb = new StringBuilder("\n*** RESUMO DO PEDIDO #" + pedidoId + " ***\n");
        if (p.getLinhas().isEmpty()) sb.append("(Carrinho vazio)\n");
        for (String s : getItensNoPedido()) sb.append("- ").append(s).append("\n");
        sb.append("----------------------------\n");
        sb.append("TOTAL A PAGAR: ").append(p.calcularPrecoTotal()).append(" EUR\n");
        return sb.toString();
    }

    public List<String> getAcompanhamento() {
        return facade.listarPedidosAtivos(restauranteId).stream()
                .map(p -> "Pedido #" + p.getId() + " - " + p.getEstado())
                .collect(Collectors.toList());
    }

    public List<String> getOpcoesPagamento() {
        return facade.listarOpcoesPagamento(restauranteId).stream().map(Enum::name).collect(Collectors.toList());
    }

    public String finalizar(int pagIndex) {
        TipoPagamento tipo = facade.listarOpcoesPagamento(restauranteId).get(pagIndex);
        Pagamento pag = facade.criarPagamento(pedidoId, tipo);
        if (tipo == TipoPagamento.TERMINAL) {
            facade.confirmarPagamento(pag.getId());
            facade.confirmarPedido(pedidoId);
            return "Pagamento TERMINAL OK. Pedido #" + pedidoId + " em preparação.";
        } else {
            facade.confirmarPedido(pedidoId);
            return "Pedido #" + pedidoId + " aguarda pagamento na CAIXA.";
        }
    }
}
package dss2526.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.TipoItem;
import dss2526.service.venda.IVendaFacade;
import dss2526.service.venda.VendaFacade;

public class VendaController {

    private final IVendaFacade vendaFacade;

    private Restaurante restauranteSelecionado;
    private Pedido pedidoAtual;
    private List<String> alergenicosAtuais;
    
    private List<Restaurante> cacheRestaurantes;
    private List<Produto> cacheProdutosDisponiveis;
    private List<Menu> cacheMenusDisponiveis;

    public VendaController() {
        this.vendaFacade = VendaFacade.getInstance();
        this.alergenicosAtuais = new ArrayList<>();
    }

    public List<String> getListaRestaurantes() {
        this.cacheRestaurantes = vendaFacade.listarRestaurantes();
        return cacheRestaurantes.stream()
                .map(r -> String.format("%-25s [ID: %d]", r.getNome(), r.getId())) 
                .collect(Collectors.toList());
    }

    public void selecionarRestaurante(int index) {
        if (cacheRestaurantes != null && index >= 0 && index < cacheRestaurantes.size()) {
            this.restauranteSelecionado = cacheRestaurantes.get(index);
        } else {
            throw new IllegalArgumentException("Restaurante inválido.");
        }
    }

    public void iniciarPedido(boolean paraLevar, List<String> alergenicos) {
        if (restauranteSelecionado == null) throw new IllegalStateException("Restaurante não selecionado.");
        this.alergenicosAtuais = alergenicos.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim).map(String::toUpperCase)
                .collect(Collectors.toList());
        this.pedidoAtual = vendaFacade.iniciarPedido(restauranteSelecionado, paraLevar);
        atualizarItensDisponiveis();
    }

    private void atualizarItensDisponiveis() {
        this.cacheProdutosDisponiveis = vendaFacade.listarProdutosDisponiveis(restauranteSelecionado, alergenicosAtuais);
        this.cacheMenusDisponiveis = vendaFacade.listarMenusDisponiveis(restauranteSelecionado, alergenicosAtuais);
    }

    public boolean isPedidoAtivo() {
        return this.pedidoAtual != null;
    }

    public void cancelarPedido() {
        if (this.pedidoAtual != null) {
            vendaFacade.cancelarPedido(this.pedidoAtual);
            this.pedidoAtual = null;
            this.alergenicosAtuais = null;
        }
    }

    public List<String> getItensDisponiveisLegiveis() {
        if (pedidoAtual == null) return new ArrayList<>();
        List<String> display = new ArrayList<>();
        String formato = "%-9s %-30s | %6.2f €";
        for (Produto p : cacheProdutosDisponiveis) display.add(String.format(formato, "[PRODUTO]", p.getNome(), p.getPreco()));
        for (Menu m : cacheMenusDisponiveis) display.add(String.format(formato, "[MENU]", m.getNome(), m.getPreco()));
        return display;
    }

    public void adicionarItemAoPedido(int indexGlobal, int quantidade) {
        if (pedidoAtual == null) return;
        LinhaPedido linha = new LinhaPedido();
        linha.setQuantidade(quantidade);
        int numProdutos = cacheProdutosDisponiveis.size();
        if (indexGlobal < numProdutos) {
            Produto p = cacheProdutosDisponiveis.get(indexGlobal);
            linha.setItemId(p.getId());
            linha.setTipo(TipoItem.PRODUTO);
            linha.setPrecoUnitario(p.getPreco());
        } else {
            int menuIndex = indexGlobal - numProdutos;
            if (menuIndex < cacheMenusDisponiveis.size()) {
                Menu m = cacheMenusDisponiveis.get(menuIndex);
                linha.setItemId(m.getId());
                linha.setTipo(TipoItem.MENU);
                linha.setPrecoUnitario(m.getPreco());
            } else return;
        }
        this.pedidoAtual = vendaFacade.adicionarLinhaAoPedido(pedidoAtual, linha);
    }

    public List<String> getResumoPedido() {
        if (pedidoAtual == null) return List.of("Nenhum pedido ativo.");
        List<String> resumo = new ArrayList<>();
        resumo.add("-------------------------------------------------------");
        resumo.add(String.format(" PEDIDO #%-4d | %s", pedidoAtual.getId(), restauranteSelecionado.getNome()));
        resumo.add("-------------------------------------------------------");
        double total = 0.0;
        int i = 0;
        String lineFormat = "%2d. %-32s  x%2d  | %6.2f €";
        for (LinhaPedido lp : pedidoAtual.getLinhas()) {
            String nomeItem = resolverNomeItem(lp);
            if (nomeItem.length() > 32) nomeItem = nomeItem.substring(0, 29) + "...";
            double subtotal = lp.getPrecoUnitario() * lp.getQuantidade(); 
            total += subtotal;
            resumo.add(String.format(lineFormat, (i + 1), nomeItem, lp.getQuantidade(), subtotal));
            i++;
        }
        resumo.add("-------------------------------------------------------");
        resumo.add(String.format(" TOTAL %35s | %6.2f €", "", total));
        resumo.add("-------------------------------------------------------");
        return resumo;
    }

    public void removerItemDoPedido(int indexLinha) {
        if (pedidoAtual != null) this.pedidoAtual = vendaFacade.removerLinhaDoPedido(pedidoAtual, indexLinha);
    }

    public String finalizarPedido() {
        if (pedidoAtual != null) {
            double minutos = vendaFacade.finalizarPedido(pedidoAtual);
            String msg = String.format("Pedido #%d Confirmado.\nTempo estimado de espera: %.0f minutos.", pedidoAtual.getId(), minutos);
            this.pedidoAtual = null;
            this.alergenicosAtuais = null;
            return msg;
        }
        return "Erro ao finalizar.";
    }

    private String resolverNomeItem(LinhaPedido lp) {
        if (lp.getTipo() == TipoItem.PRODUTO) {
            Produto p = vendaFacade.obterProduto(lp.getItemId());
            return p != null ? p.getNome() : "Produto " + lp.getItemId();
        } else {
            Menu m = vendaFacade.obterMenu(lp.getItemId());
            return m != null ? m.getNome() : "Menu " + lp.getItemId();
        }
    }
}
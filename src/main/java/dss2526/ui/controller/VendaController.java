package dss2526.ui.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.TipoItem;
import dss2526.service.venda.*;

public class VendaController {

    private IVendaFacade vendaFacade;

    private Pedido pedidoAtual;

    public VendaController() {
        this.vendaFacade = VendaFacade.getInstance();
    }

    public void iniciarPedido(Integer restauranteIndex, Boolean paraLevar) {
        Restaurante restaurante = vendaFacade.listarRestaurantes().get(restauranteIndex);
        pedidoAtual = vendaFacade.iniciarPedido(restaurante, paraLevar);
    }

    public List<String> listarItensDisponiveis(Integer restauranteIndex) {
        Restaurante restaurante = vendaFacade.listarRestaurantes().get(restauranteIndex);
        List<Produto> produtos = vendaFacade.listarProdutosDisponiveis(restaurante);
        List<Menu> menus = vendaFacade.listarMenusDisponiveis(restaurante);
        return Stream.concat(
            produtos.stream().map(Object::toString),
            menus.stream().map(Object::toString)
        ).collect(Collectors.toList());
    }

    public List<String> listarItensDoPedido() {
        return pedidoAtual.getLinhas().stream()
                .map(LinhaPedido::toString)
                .toList();
    }

    public void adicionarItemAoPedido(Integer restauranteIndex, Integer itemIndex, Integer quantidade) {
        Restaurante restaurante = vendaFacade.listarRestaurantes().get(restauranteIndex);
        List<Produto> produtos = vendaFacade.listarProdutosDisponiveis(restaurante);
        List<Menu> menus = vendaFacade.listarMenusDisponiveis(restaurante);
        if (itemIndex < produtos.size()) {
            Produto produto = produtos.get(itemIndex);
            LinhaPedido linha = new LinhaPedido();
            linha.setItemId(produto.getId());
            linha.setTipo(TipoItem.PRODUTO);
            linha.setQuantidade(quantidade);
            pedidoAtual.addLinha(linha);
        } else {
            Menu menu = menus.get(itemIndex - produtos.size());
            LinhaPedido linha = new LinhaPedido();
            linha.setItemId(menu.getId());
            linha.setTipo(TipoItem.MENU);
            linha.setQuantidade(quantidade);
            pedidoAtual.addLinha(linha);
        }
        vendaFacade.atualizarPedido(pedidoAtual);
    }

    public void removerItemDoPedido(Integer restauranteIndex, Integer itemIndex) {
        if (itemIndex >= 0 && itemIndex < pedidoAtual.getLinhas().size()) {
            LinhaPedido linha = pedidoAtual.getLinhas().get(itemIndex);
            pedidoAtual.removeLinha(linha);
            vendaFacade.atualizarPedido(pedidoAtual);
        }
    }

    public List<String> consultarPedido() {
        List<String> detalhes = pedidoAtual.getLinhas().stream()
                .map(LinhaPedido::toString)
                .collect(Collectors.toList());  // Mutable ArrayList
        detalhes.addFirst(pedidoAtual.toString());
        return detalhes;
    }

    public void finalizarPedido() {
        vendaFacade.finalizarPedido(pedidoAtual);
    }

    public List<String> listarRestaurantes() {
        List<Restaurante> restaurantes = vendaFacade.listarRestaurantes();
        return restaurantes.stream()
                .map(r -> r.toString())
                .toList();
    }
}
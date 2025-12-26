package dss2526.ui.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dss2526.domain.entity.*;
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

    public List<String> listarRestaurantes() {
        List<Restaurante> restaurantes = vendaFacade.listarRestaurantes();
        return restaurantes.stream()
                .map(r -> r.toString())
                .toList();
    }
}
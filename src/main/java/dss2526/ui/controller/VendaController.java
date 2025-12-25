package dss2526.ui.controller;

import dss2526.domain.entity.Pedido;
import dss2526.service.venda.IVendaFacade;
import dss2526.service.venda.VendaFacade;

public class VendaController {

    private IVendaFacade vendaFacade;
    private Pedido pedidoAtual;

    public VendaController() {
        // Connect to the Singleton Facade
        this.vendaFacade = VendaFacade.getInstance();
    }

    public void novoPedido(int restauranteId) {
        this.pedidoAtual = vendaFacade.iniciarPedido(restauranteId);
    }

    public void finalizarPedido() {
        if (pedidoAtual != null) {
            vendaFacade.fecharPedido(pedidoAtual.getId());
        }
    }
}
package dss2526.service.venda;

import dss2526.domain.entity.Pedido;

public interface IVendaFacade {
    Pedido iniciarPedido(int restauranteId);
    void adicionarItem(int pedidoId, int itemId, int quantidade);
    void fecharPedido(int pedidoId);
}
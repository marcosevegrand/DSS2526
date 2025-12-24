package dss2526.venda;

import dss2526.domain.entity.Pedido;

public interface IVendaFacade {

    Pedido criarPedido(boolean paraLevar);

    void adicionarItem(int idPedido, int idItem, int quantidade, String observacao);

    void removerItem(int idPedido, int idItem, int quantidade);

    void adicionarNota(int idPedido, String nota);

    void confirmarPedido(int idPedido);

    void cancelarPedido(int idPedido);

    Pedido obterPedido(int idPedido);

    List<Item> obterItemsDisponiveis();
}
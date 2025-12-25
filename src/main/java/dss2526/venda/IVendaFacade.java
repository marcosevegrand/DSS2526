package dss2526.venda;

import dss2526.domain.entity.Pedido;
import dss2526.domain.contract.Item;
import java.util.List;

public interface IVendaFacade {
    // Mantemos a versão com restauranteId pois o teu Pedido precisa dele
    Pedido criarPedido(int restauranteId, boolean paraLevar);

    void adicionarItem(int idPedido, int idItem, int quantidade, String observacao);

    void removerItem(int idPedido, int idItem, int quantidade);

    void adicionarNota(int idPedido, String nota);

    void confirmarPedido(int idPedido);

    void cancelarPedido(int idPedido);

    Pedido obterPedido(int idPedido);

    List<Item> obterItemsDisponiveis();
}
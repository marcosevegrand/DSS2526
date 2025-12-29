package dss2526.service.venda;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;
import dss2526.domain.contract.Item;
import dss2526.service.base.IBaseFacade;
import java.util.List;

public interface IVendaFacade extends IBaseFacade {
    Pedido iniciarPedido(int restauranteId);
    List<Ingrediente> listarAlergenicosDisponiveis();
    List<Item> listarCatalogoFiltrado(int restauranteId, List<Integer> excluirAlergenicosIds);
    void adicionarItemAoPedido(int pedidoId, int itemId, TipoItem tipo, int qtd, String obs);
    void removerItemDoPedido(int pedidoId, int linhaId);
    void cancelarPedido(int pedidoId);
    Pagamento processarPagamento(int pedidoId, TipoPagamento tipo);
    List<Pedido> listarPedidosAtivos(int restauranteId);
}
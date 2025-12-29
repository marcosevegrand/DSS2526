package dss2526.service.venda;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;
import dss2526.domain.contract.Item;
import dss2526.service.base.IBaseFacade;
import java.util.List;

public interface IVendaFacade extends IBaseFacade {
    // Fluxo Inicial
    Pedido iniciarPedido(int restauranteId);
    List<Ingrediente> listarAlergenicosDisponiveis();
    
    // Listagem Unificada
    List<Item> listarItemsDisponiveis(int restauranteId, List<Integer> alergenicoIds);
    
    // Gestão de Pedido
    boolean adicionarLinhaAoPedido(int pedidoId, int itemId, TipoItem tipo, int quantidade, String observacao);
    
    /**
     * Remove uma linha específica do pedido em curso.
     */
    boolean removerLinhaDoPedido(int pedidoId, int linhaPedidoId);

    /**
     * Cancela o pedido em curso, mudando o seu estado para CANCELADO.
     */
    void cancelarPedidoVenda(int pedidoId);
    
    double obterEstimativaEntrega(int pedidoId);
    
    // Acompanhamento
    List<Pedido> listarPedidosAtivos(int restauranteId);
    
    // Pagamento
    List<TipoPagamento> listarOpcoesPagamento(int restauranteId);
    Pagamento criarPagamento(int pedidoId, TipoPagamento tipo);
    boolean confirmarPagamento(int pagamentoId);
    boolean confirmarPedido(int pedidoId);
}
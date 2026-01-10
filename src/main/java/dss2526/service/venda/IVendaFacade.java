package dss2526.service.venda;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;
import dss2526.domain.contract.Item;
import java.time.Duration;
import java.util.List;

/**
 * Interface para o módulo de Venda.
 * Define apenas os métodos necessários para o módulo de venda/ponto de venda.
 *
 * NOTA: Não herda de IBaseFacade para respeitar o Interface Segregation Principle.
 */
public interface IVendaFacade {

    // ============ LISTAGENS ============
    List<Ingrediente> listarAlergenicosDisponiveis();
    List<Restaurante> listarRestaurantes();

    // ============ FLUXO DE PEDIDO ============
    Pedido iniciarPedido(int restauranteId);
    Pedido obterPedido(int pedidoId);
    List<Item> listarCatalogoFiltrado(int restauranteId, List<Integer> excluirAlergenicosIds);
    void adicionarItemAoPedido(int pedidoId, int itemId, TipoItem tipo, int quantidade, String observacao);
    void removerQuantidadeDoPedido(int pedidoId, int linhaId, int quantidade);
    void cancelarPedido(int pedidoId);
    Duration processarPagamento(int pedidoId, TipoPagamento tipo);

    // ============ MONITORIZAÇÃO ============
    List<Pedido> listarPedidosAtivos(int restauranteId);

    // ============ UTILITÁRIOS ============
    String obterNomeItem(int itemId, TipoItem tipo);
}
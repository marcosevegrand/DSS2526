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
    /**
     * Lista todos os ingredientes que tém alérgenos associados.
     * @return Lista de ingredientes alergenicos
     */
    List<Ingrediente> listarAlergenicosDisponiveis();
    
    /**
     * Lista todos os restaurantes.
     * @return Lista de restaurantes
     */
    List<Restaurante> listarRestaurantes();
    
    // ============ FLUXO DE PEDIDO ============
    /**
     * Inicia um novo pedido para um restaurante.
     * @param restauranteId ID do restaurante
     * @return Pedido criado
     */
    Pedido iniciarPedido(int restauranteId);
    
    /**
     * Obtém um pedido pelo seu ID.
     * @param pedidoId ID do pedido
     * @return Pedido ou null se não encontrado
     */
    Pedido obterPedido(int pedidoId);
    
    /**
     * Lista o catálogo filtrado por alérgenos e stock.
     * @param restauranteId ID do restaurante
     * @param excluirAlergenicosIds IDs dos alérgenos a excluir
     * @return Lista de Items (Produtos ou Menus) disponíveis
     */
    List<Item> listarCatalogoFiltrado(int restauranteId, List<Integer> excluirAlergenicosIds);
    
    /**
     * Adiciona um item (Produto ou Menu) a um pedido.
     * @param pedidoId ID do pedido
     * @param itemId ID do item (Produto ou Menu)
     * @param tipo Tipo do item (PRODUTO ou MENU)
     * @param quantidade Quantidade desejada
     * @param observacao Observações adicionais
     */
    void adicionarItemAoPedido(int pedidoId, int itemId, TipoItem tipo, int quantidade, String observacao);
    
    /**
     * Remove uma quantidade de um item do pedido.
     * @param pedidoId ID do pedido
     * @param linhaId ID da linha do pedido
     * @param quantidade Quantidade a remover
     */
    void removerQuantidadeDoPedido(int pedidoId, int linhaId, int quantidade);
    
    /**
     * Cancela um pedido (apenas se ainda não foi confirmado).
     * @param pedidoId ID do pedido
     */
    void cancelarPedido(int pedidoId);
    
    /**
     * Processa o pagamento de um pedido.
     * @param pedidoId ID do pedido
     * @param tipo Tipo de pagamento (TERMINAL ou CAIXA)
     * @return Estimação de tempo de espera (null se pagamento não confirmado)
     */
    Duration processarPagamento(int pedidoId, TipoPagamento tipo);
    
    // ============ MONITORIZAÇÃO ============
    /**
     * Lista todos os pedidos ativos de um restaurante.
     * @param restauranteId ID do restaurante
     * @return Lista de pedidos em estados CONFIRMADO, EM_PREPARACAO ou PRONTO
     */
    List<Pedido> listarPedidosAtivos(int restauranteId);
    
    // ============ UTILITÁRIOS ============
    /**
     * Obtém o nome de um item (Produto ou Menu).
     * @param itemId ID do item
     * @param tipo Tipo do item (PRODUTO ou MENU)
     * @return Nome do item ou "Desconhecido" se não encontrado
     */
    String obterNomeItem(int itemId, TipoItem tipo);
}

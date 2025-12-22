package restaurante.business.pedidos;

/**
 * Enumeration representing the possible states of an order
 */
public enum EstadoPedido {
    AGUARDA_PAGAMENTO,
    EM_PREPARACAO,
    PRONTO,
    ENTREGUE,
    CANCELADO,
    NAO_RECLAMADO
}

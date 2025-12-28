package dss2526.domain.enumeration;

public enum EstadoPedido {
    INICIADO,       // Cliente começou a escolher
    CONFIRMADO,     // Cliente pagou/confirmou (vai para a cozinha)
    EM_PREPARACAO,  // Cozinha começou a trabalhar
    PRONTO,         // Cozinha terminou, aguarda entrega
    ENTREGUE,       // Cliente recebeu o pedido
    CANCELADO       // Anulado
}

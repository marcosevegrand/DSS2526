package dss2526.domain.enumeration;

public enum EstadoPedido {
    INICIADO,           // Cliente em processo de escolha
    AGUARDA_PAGAMENTO,  // Pedido finalizado mas aguarda pagamento na caixa
    CONFIRMADO,         // Pago/Confirmado (Pronto para gerar tarefas)
    EM_PREPARACAO,      // Cozinha a trabalhar nas tarefas
    PRONTO,             // Todas as tarefas concluídas, aguarda entrega
    ENTREGUE,           // Entregue ao cliente
    CANCELADO           // Anulado
}
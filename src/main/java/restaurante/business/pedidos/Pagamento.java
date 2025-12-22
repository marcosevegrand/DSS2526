package restaurante.business.pedidos;

import java.time.LocalDateTime;

/**
 * Represents a payment for an order
 */
public class Pagamento {
    private String id;
    private double valor;
    private String metodoPagamento;
    private LocalDateTime dataHora;
    private boolean processado;
    
    public Pagamento(String id, double valor, String metodoPagamento) {
        this.id = id;
        this.valor = valor;
        this.metodoPagamento = metodoPagamento;
        this.dataHora = LocalDateTime.now();
        this.processado = false;
    }
    
    public String getId() {
        return id;
    }
    
    public double getValor() {
        return valor;
    }
    
    public String getMetodoPagamento() {
        return metodoPagamento;
    }
    
    public LocalDateTime getDataHora() {
        return dataHora;
    }
    
    public boolean isProcessado() {
        return processado;
    }
    
    public void setProcessado(boolean processado) {
        this.processado = processado;
    }
}

package dss2526.domain.entity;

import dss2526.domain.enumeration.TipoPagamento;
import java.time.LocalDateTime;

public class Pagamento {
    private int id;
    private int pedidoId;
    private double valor;
    private TipoPagamento tipo;
    private boolean confirmado;
    private LocalDateTime dataPagamento;

    public Pagamento() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPedidoId() { return pedidoId; }
    public void setPedidoId(int pedidoId) { this.pedidoId = pedidoId; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public TipoPagamento getTipo() { return tipo; }
    public void setTipo(TipoPagamento tipo) { this.tipo = tipo; }
    public boolean isConfirmado() { return confirmado; }
    public void setConfirmado(boolean confirmado) { this.confirmado = confirmado; }
    public LocalDateTime getData() { return dataPagamento; }
    public void setData(LocalDateTime dataPagamento) { this.dataPagamento = dataPagamento; }
}
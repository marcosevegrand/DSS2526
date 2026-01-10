package dss2526.domain.entity;

import dss2526.domain.enumeration.TipoItem;

public class LinhaPedido {
    private int id;
    private int pedidoId;
    private int itemId;
    private TipoItem tipo;
    private int quantidade;
    private double precoUnitario;
    private String observacao;

    public LinhaPedido() {}

    public double getPreco() {
        return precoUnitario * quantidade;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPedidoId() { return pedidoId; }
    public void setPedidoId(int pedidoId) { this.pedidoId = pedidoId; }
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    public TipoItem getTipo() { return tipo; }
    public void setTipo(TipoItem tipo) { this.tipo = tipo; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(double precoUnitario) { this.precoUnitario = precoUnitario; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
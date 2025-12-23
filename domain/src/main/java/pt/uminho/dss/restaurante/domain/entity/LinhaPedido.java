package pt.uminho.dss.restaurante.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class LinhaPedido implements Serializable {
    private Long id;
    private Pedido pedido;
    private Long itemId; 
    private String nomeItem;
    private Integer quantidade;
    private BigDecimal precoUnitario;

    public LinhaPedido() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public String getNomeItem() { return nomeItem; }
    public void setNomeItem(String nomeItem) { this.nomeItem = nomeItem; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }
}
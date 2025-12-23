package pt.uminho.dss.restaurante.domain.entity;

import pt.uminho.dss.restaurante.domain.contract.Item;

import java.io.Serializable;
import java.math.BigDecimal;

public class LinhaPedido implements Serializable {
    private Integer id;
    private Item item;
    private Integer quantidade;
    private BigDecimal precoUnitario;

    // Construtores

    public LinhaPedido() {}
    
    public LinhaPedido(Item item, Integer quantidade, BigDecimal precoUnitario) {
        this.item = item;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    // Lógica simples

    public BigDecimal getPreco() {
        return precoUnitario.multiply(new BigDecimal(quantidade));
    }

    // Getters e Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }
}

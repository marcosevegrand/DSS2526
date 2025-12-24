package dss2526.domain.entity;

import dss2526.domain.contract.Item;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class LinhaPedido implements Serializable {
    private Integer id;
    private Item item;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private String observacao;

    // Construtores

    public LinhaPedido() {
        this.observacao = "";
    }

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

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LinhaPedido)) return false;
        LinhaPedido that = (LinhaPedido) o;
        return Objects.equals(item, that.item) && 
               Objects.equals(observacao, that.observacao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, observacao);
    }
}

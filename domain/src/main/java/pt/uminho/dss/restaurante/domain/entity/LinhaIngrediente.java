package pt.uminho.dss.restaurante.domain.entity;

import java.io.Serializable;

public class LinhaIngrediente implements Serializable {
    private Long id;
    private Produto produto;
    private Ingrediente ingrediente;
    private Double quantidade;

    public LinhaIngrediente() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Ingrediente getIngrediente() { return ingrediente; }
    public void setIngrediente(Ingrediente ingrediente) { this.ingrediente = ingrediente; }
    
    public Double getQuantidade() { return quantidade; }
    public void setQuantidade(Double quantidade) { this.quantidade = quantidade; }
}
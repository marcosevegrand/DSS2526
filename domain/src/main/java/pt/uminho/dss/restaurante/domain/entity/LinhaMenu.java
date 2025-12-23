package pt.uminho.dss.restaurante.domain.entity;

import java.io.Serializable;

public class LinhaMenu implements Serializable {
    private Long id;
    private Produto produto;
    private Integer quantidade;

    // Construtores

    public LinhaMenu() {}

    public LinhaMenu(Produto produto, Integer quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    // Getters e Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}
package dss2526.domain.entity;

import java.io.Serializable;

public class LinhaMenu implements Serializable {
    private Integer id;
    private Produto produto;
    private Integer quantidade;

    // Construtores

    public LinhaMenu() {}

    public LinhaMenu(Produto produto, Integer quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    // Getters e Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}
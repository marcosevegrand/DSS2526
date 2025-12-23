package pt.uminho.dss.restaurante.domain.entity;

import java.io.Serializable;

public class LinhaMenu implements Serializable {
    private Long id;
    private Menu menu;
    private Produto produto;
    private Integer quantidade;

    public LinhaMenu() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Menu getMenu() { return menu; }
    public void setMenu(Menu menu) { this.menu = menu; }
    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}
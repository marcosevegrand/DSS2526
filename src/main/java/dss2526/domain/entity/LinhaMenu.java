package dss2526.domain.entity;

public class LinhaMenu {
    private int id;
    private Produto produto;
    private int quantidade;

    // Construtores

    public LinhaMenu() {}

    public LinhaMenu(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}
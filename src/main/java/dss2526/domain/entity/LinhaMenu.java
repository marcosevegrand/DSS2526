package dss2526.domain.entity;

public class LinhaMenu {
    private int id;
    private int idProduto;
    private int quantidade;

    // Construtores

    public LinhaMenu() {}

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdProduto() { return idProduto; }
    public void setIdProduto(int idProduto) { this.idProduto = idProduto; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}
package dss2526.domain.entity;

public class LinhaPedido {
    private int id;
    private int idItem;
    private int quantidade;
    private double precoUnitario;
    private String observacao;

    // Construtores

    public LinhaPedido() {}

    public LinhaPedido(int idItem, int quantidade, double precoUnitario, String observacao) {
        this.idItem = idItem;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.observacao = (observacao == null) ? "" : observacao;
    }

    // Lógica simples

    public double getPreco() {
        return precoUnitario * quantidade;
    }

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdItem() { return idItem; }
    public void setIdItem(int idItem) { this.idItem = idItem; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(double precoUnitario) { this.precoUnitario = precoUnitario; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}

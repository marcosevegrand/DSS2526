package dss2526.domain.entity;

import dss2526.domain.contract.Item;

public class LinhaPedido {
    private int id;
    private Item item;
    private int quantidade;
    private double precoUnitario;
    private String observacao;

    // Construtores

    public LinhaPedido() {
        this.observacao = "";
    }

    public LinhaPedido(Item item, int quantidade, double precoUnitario, String observacao) {
        this.item = item;
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

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(double precoUnitario) { this.precoUnitario = precoUnitario; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}

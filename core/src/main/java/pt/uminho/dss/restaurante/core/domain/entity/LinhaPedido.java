package pt.uminho.dss.restaurante.core.domain.entity;

import pt.uminho.dss.restaurante.core.domain.contract.Item;

public class LinhaPedido {

    private int id;
    private Item item;
    private int quantidade;
    private String personalizacao;
    private float precoLinha;
    private int tempoPreparacao;

    public LinhaPedido(Item item, int quantidade, String personalizacao) {
        this.item = item;
        this.quantidade = quantidade;
        this.personalizacao = personalizacao;
        recalcularTotais();
    }

    public void recalcularTotais() {
        this.precoLinha = item.calcularPreco(quantidade, personalizacao);
        this.tempoPreparacao = item.calcularTempoPreparacao(
            quantidade,
            personalizacao
        );
    }

    // getters / setters…

    public int getId() {
        return id;
    }

    public Item getItem() {
        return item;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getPersonalizacao() {
        return personalizacao;
    }

    public void setPersonalizacao(String personalizacao) {
        this.personalizacao = personalizacao;
    }

    public float getPrecoLinha() {
        return precoLinha;
    }

    public int getTempoPreparacao() {
        return tempoPreparacao;
    }
}

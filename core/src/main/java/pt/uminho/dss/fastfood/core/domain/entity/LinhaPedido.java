package pt.uminho.dss.fastfood.core.domain.entity;

import pt.uminho.dss.fastfood.core.domain.entity.Produto;

public class LinhaPedido {

    private int id;
    private Produto item; // ou Menu/ProdutoOuMenu, conforme tiveres
    private int quantidade;
    private String personalizacao; // ex.: "sem tomate; topping chocolate"

    private float precoLinha;
    private int tempoPreparacao; // em minutos

    public LinhaPedido(Produto item, int quantidade, String personalizacao) {
        this.item = item;
        this.quantidade = quantidade;
        this.personalizacao = personalizacao;
        recalcularTotais();
    }

    protected LinhaPedido() {}

    public void recalcularTotais() {
        // Ajusta isto à tua lógica real
        this.precoLinha = item.getPrecoBase() * quantidade;
        this.tempoPreparacao = item.getTempoPreparacaoBase();
    }

    // Getters e setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        // se o ID for gerido pela BD/ORM, podes remover este setter
        this.id = id;
    }

    public Produto getItem() {
        return item;
    }

    public void setItem(Produto item) {
        this.item = item;
        recalcularTotais();
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
        recalcularTotais();
    }

    public String getPersonalizacao() {
        return personalizacao;
    }

    public void setPersonalizacao(String personalizacao) {
        this.personalizacao = personalizacao;
        recalcularTotais();
    }

    public float getPrecoLinha() {
        return precoLinha;
    }

    public void setPrecoLinha(float precoLinha) {
        // normalmente não precisas deste setter
        this.precoLinha = precoLinha;
    }

    public int getTempoPreparacao() {
        return tempoPreparacao;
    }

    public void setTempoPreparacao(int tempoPreparacao) {
        // idem, pode ser só calculado
        this.tempoPreparacao = tempoPreparacao;
    }
}

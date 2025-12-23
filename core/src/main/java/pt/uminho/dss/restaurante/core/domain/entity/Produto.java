package pt.uminho.dss.restaurante.core.domain.entity;

import java.util.Map;
import java.util.Set;
import pt.uminho.dss.restaurante.core.domain.contract.Item;

public class Produto implements Item {

    private int id;
    private String nome;
    private float preco;
    private int tempoPreparacao;
    private Set<Ingrediente> ingredientes;
    private Map<Integer, Integer> ingredientesQuantidade;

    // -------------------------------------------------
    // Construtores
    // -------------------------------------------------

    public Produto(
        int id,
        String nome,
        float preco,
        Set<Ingrediente> ingredientes,
        Map<Integer, Integer> ingredientesQuantidade
    ) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        for (Ingrediente i : ingredientes) {
            this.ingredientes.add(i);
            this.ingredientesQuantidade.put(
                i.getId(),
                ingredientesQuantidade.get(i.getId())
            );
            this.tempoPreparacao += i.getTempoPreparacao();
        }
    }

    // Construtor vazio para ORM / frameworks
    protected Produto() {}

    // -------------------------------------------------
    // Getters e setters
    // -------------------------------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public float getPreco() {
        return preco;
    }

    public void setPreco(float preco) {
        this.preco = preco;
    }

    public int getTempoPreparacao() {
        return tempoPreparacao;
    }

    public void setTempoPreparacao(int tempoPreparacaoBase) {
        this.tempoPreparacao = tempoPreparacaoBase;
    }
}

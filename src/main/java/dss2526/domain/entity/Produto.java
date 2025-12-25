package dss2526.domain.entity;

import dss2526.domain.contract.Item;

import java.util.*;

public class Produto implements Item {
    private int id;
    private String nome;
    private double preco;
    private List<LinhaProduto> ingredientes = new ArrayList<>();
    private List<Tarefa> tarefas = new ArrayList<>();

    // Construtores

    public Produto() {
    }

    public Produto(String nome, int preco, List<LinhaProduto> ingredientes, List<Tarefa> tarefas) {
        this.nome = nome;
        this.preco = preco;
        this.ingredientes = ingredientes;
        this.tarefas = tarefas;
    }

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public List<LinhaProduto> getIngredientes() { return ingredientes; }
    public void setIngredientes(List<LinhaProduto> ingredientes) { this.ingredientes = ingredientes; }

    public List<Tarefa> getTarefas() { return tarefas; }
    public void setTarefas(List<Tarefa> tarefas) { this.tarefas = tarefas; }
}

package dss2526.domain.entity;

import dss2526.domain.contract.Item;
import java.util.*;

public class Produto implements Item {
    private int id;
    private String nome;
    private double preco;
    private boolean disponivel = true; // Adicionado para satisfazer a interface Item
    private List<LinhaProduto> ingredientes = new ArrayList<>();
    private List<Tarefa> tarefas = new ArrayList<>();

    // Construtores

    public Produto() {
    }

    // Corrigido: preco de int para double para manter consistência
    public Produto(String nome, double preco, List<LinhaProduto> ingredientes, List<Tarefa> tarefas) {
        this.nome = nome;
        this.preco = preco;
        this.ingredientes = ingredientes;
        this.tarefas = tarefas;
        this.disponivel = true;
    }

    // Getters e Setters

    @Override
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @Override
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    @Override
    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    // Implementação obrigatória do método da interface Item
    @Override
    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }

    public List<LinhaProduto> getIngredientes() { return ingredientes; }
    public void setIngredientes(List<LinhaProduto> ingredientes) { this.ingredientes = ingredientes; }

    public List<Tarefa> getTarefas() { return tarefas; }
    public void setTarefas(List<Tarefa> tarefas) { this.tarefas = tarefas; }
}
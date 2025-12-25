package dss2526.domain.entity;

import dss2526.domain.enumeration.Trabalho;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Passo {
    private int id;
    private String nome;
    private Duration duracao;
    private Trabalho trabalho;
    private List<Integer> ingredienteIds = new ArrayList<>();

    // Construtores
    public Passo() {}

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Duration getDuracao() { return duracao; }
    public void setDuracao(Duration duracao) { this.duracao = duracao; }

    public Trabalho getTrabalho() { return trabalho; }
    public void setTrabalho(Trabalho trabalho) { this.trabalho = trabalho; }

    public List<Integer> getIngredienteIds() { return ingredienteIds; }
    public void setIngredienteIds(List<Integer> ingredienteIds) { this.ingredienteIds = ingredienteIds; }
    public void addIngredienteId(Integer ingredienteId) { this.ingredienteIds.add(ingredienteId); }
    public void removeIngredienteId(Integer ingredienteId) { this.ingredienteIds.remove(ingredienteId); }
}
package dss2526.domain.entity;

import dss2526.domain.enumeration.Trabalho;

import java.time.Duration;
import java.util.*;

public class Tarefa {
    private int id;
    private String nome;
    private Duration duracao;
    private Trabalho trabalho;

    // Construtores
    public Tarefa() {}

    public Tarefa(String nome, Duration duracao, Trabalho trabalho) {
        this.nome = nome;
        this.duracao = duracao;
        this.trabalho = trabalho;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Duration getDuracao() { return duracao; }
    public void setDuracao(Duration duracao) { this.duracao = duracao; }

    public Trabalho getTrabalho() { return trabalho; }
    public void setTrabalho(Trabalho trabalho) { this.trabalho = trabalho; }
}
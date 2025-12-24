package dss2526.domain.entity;

import dss2526.domain.enumeration.Trabalho;

public class Tarefa {
    private int id;
    private String nome;
    private Trabalho trabalho;

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Trabalho getTrabalho() { return trabalho; }
    public void setTrabalho(Trabalho trabalho) { this.trabalho = trabalho; }

}
package dss2526.domain.entity;

import dss2526.domain.enumeration.Trabalho;

import java.util.*;

public class Estacao {
    private int id;
    private Trabalho trabalho;
    private List<LinhaEstacao> tarefas;

    // Construtores

    public Estacao() {}

    public Estacao(Trabalho trabalho, List<LinhaEstacao> tarefas) {
        this.trabalho = trabalho;
        this.tarefas = tarefas;
    }

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Trabalho getTrabalho() { return trabalho; }
    public void setTrabalho(Trabalho trabalho) { this.trabalho = trabalho; }

    public List<LinhaEstacao> getLinhaEstacaos() { return tarefas; }
    public void setLinhaEstacaos(List<LinhaEstacao> tarefas) { this.tarefas = tarefas; }

}

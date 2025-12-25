package dss2526.domain.entity;

import dss2526.domain.enumeration.Trabalho;
import java.util.*;

public class Estacao {
    private int id;
    private int restauranteId;
    private Trabalho trabalho;
    private List<LinhaEstacao> tarefas = new ArrayList<>();

    // Construtores

    public Estacao() {}

    // Lógica de Apoio aos Cenários

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRestauranteId() { return restauranteId; }
    public void setRestauranteId(int restauranteId) { this.restauranteId = restauranteId; }

    public Trabalho getTrabalho() { return trabalho; }
    public void setTrabalho(Trabalho trabalho) { this.trabalho = trabalho; }

    public List<LinhaEstacao> getLinhaEstacaos() { return tarefas; }
    public void setLinhaEstacaos(List<LinhaEstacao> tarefas) { this.tarefas = tarefas; }
}
package dss2526.domain.entity;

import dss2526.domain.enumeration.Trabalho;
import java.util.*;

public class Estacao {
    private int id;
    private int restauranteId; // Fundamental para isolar a produção por unidade física
    private Trabalho trabalho; // Define a função (Grelha, Fritura, etc.)
    private List<LinhaEstacao> tarefas;

    // Construtores

    public Estacao() {
        this.tarefas = new ArrayList<>();
    }

    public Estacao(int restauranteId, Trabalho trabalho) {
        this();
        this.restauranteId = restauranteId;
        this.trabalho = trabalho;
    }

    // Lógica de Apoio aos Cenários

    /**
     * Retorna apenas as tarefas que ainda não foram concluídas.
     */
    public List<LinhaEstacao> obterFilaEspera() {
        return this.tarefas; 
    }

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
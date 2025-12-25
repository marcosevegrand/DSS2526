package dss2526.domain.entity;

import dss2526.domain.enumeration.Trabalho;
import java.time.LocalDateTime;

public class Tarefa {
    private int id;
    private int pedidoId;       // FK para o Pedido
    private int restauranteId;  // FK para o Restaurante (Contexto)
    private String nome;        // Ex: "Grelhar Hambúrguer"
    private Trabalho trabalho;  // Ex: GRELHADOS, FRITOS (para as abas da UI)
    private boolean concluida;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataFim;

    public Tarefa() {
        this.dataCriacao = LocalDateTime.now();
        this.concluida = false;
    }

    public Tarefa(int pedidoId, int restauranteId, String nome, Trabalho trabalho) {
        this();
        this.pedidoId = pedidoId;
        this.restauranteId = restauranteId;
        this.nome = nome;
        this.trabalho = trabalho;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPedidoId() { return pedidoId; }
    public void setPedidoId(int pedidoId) { this.pedidoId = pedidoId; }

    public int getRestauranteId() { return restauranteId; }
    public void setRestauranteId(int restauranteId) { this.restauranteId = restauranteId; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Trabalho getTrabalho() { return trabalho; }
    public void setTrabalho(Trabalho trabalho) { this.trabalho = trabalho; }

    public boolean isConcluida() { return concluida; }
    public void setConcluida(boolean concluida) { 
        this.concluida = concluida; 
        if (concluida) this.dataFim = LocalDateTime.now();
    }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
}
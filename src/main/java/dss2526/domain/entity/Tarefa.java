package dss2526.domain.entity;

import java.time.LocalDateTime;

public class Tarefa {
    int id;
    int passoId;
    int pedidoId;
    LocalDateTime dataCriacao;
    LocalDateTime dataConclusao;
    boolean concluido;

    // Construtores
    
    public Tarefa() {}

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPassoId() { return passoId; }
    public void setPassoId(int passoId) { this.passoId = passoId; }

    public int getPedidoId() { return pedidoId; }
    public void setPedidoId(int pedidoId) { this.pedidoId = pedidoId; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDateTime dataConclusao) { this.dataConclusao = dataConclusao; }

    public boolean isConcluido() { return concluido; }
    public void setConcluido(boolean concluido) { this.concluido = concluido; }
}
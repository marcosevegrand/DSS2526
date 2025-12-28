package dss2526.domain.entity;

import java.time.LocalDateTime;

import dss2526.domain.enumeration.EstadoTarefa;

public class Tarefa {
    int id;
    int passoId;
    int produtoId;
    int pedidoId;
    int estacaoId;
    EstadoTarefa estado;
    LocalDateTime dataCriacao;
    LocalDateTime dataInicio;
    LocalDateTime dataConclusao;

    // Construtores
    
    public Tarefa() {}

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPassoId() { return passoId; }
    public void setPassoId(int passoId) { this.passoId = passoId; }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public int getPedidoId() { return pedidoId; }
    public void setPedidoId(int pedidoId) { this.pedidoId = pedidoId; }

    public EstadoTarefa getEstado() { return estado; }
    public void setEstado(EstadoTarefa estado) { this.estado = estado; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDateTime dataConclusao) { this.dataConclusao = dataConclusao; }
}
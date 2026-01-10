package dss2526.domain.entity;

import java.time.LocalDateTime;
import dss2526.domain.enumeration.EstadoTarefa;

public class Tarefa {
    private int id;
    private int passoId;
    private int produtoId;
    private int pedidoId;
    private int estacaoId;
    private EstadoTarefa estado;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataConclusao;

    public Tarefa() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPassoId() { return passoId; }
    public void setPassoId(int passoId) { this.passoId = passoId; }
    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }
    public int getPedidoId() { return pedidoId; }
    public void setPedidoId(int pedidoId) { this.pedidoId = pedidoId; }
    public int getEstacaoId() { return estacaoId; }
    public void setEstacaoId(int estacaoId) { this.estacaoId = estacaoId; }
    public EstadoTarefa getEstado() { return estado; }
    public void setEstado(EstadoTarefa estado) { this.estado = estado; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
    public LocalDateTime getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDateTime dataConclusao) { this.dataConclusao = dataConclusao; }
}
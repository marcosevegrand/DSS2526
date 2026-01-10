package dss2526.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import dss2526.domain.enumeration.EstadoPedido;

public class Pedido {
    private int id;
    private int restauranteId;
    private boolean paraLevar;
    private EstadoPedido estado;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataConclusao;
    private List<LinhaPedido> linhas = new ArrayList<>();

    public Pedido() {}

    public double calcularPrecoTotal() {
        return linhas.stream().mapToDouble(LinhaPedido::getPreco).sum();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getRestauranteId() { return restauranteId; }
    public void setRestauranteId(int restauranteId) { this.restauranteId = restauranteId; }
    public boolean isParaLevar() { return paraLevar; }
    public void setParaLevar(boolean paraLevar) { this.paraLevar = paraLevar; }
    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDateTime getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDateTime dataConclusao) { this.dataConclusao = dataConclusao; }
    public List<LinhaPedido> getLinhas() { return linhas; }
    public void setLinhas(List<LinhaPedido> linhas) { this.linhas = linhas; }
    public void addLinha(LinhaPedido linha) { this.linhas.add(linha); }
    public void removeLinha(LinhaPedido linha) { this.linhas.remove(linha); }
}
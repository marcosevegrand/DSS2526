package dss2526.domain.entity;

import dss2526.domain.enumeration.EstadoPedido;
import java.time.LocalDateTime;
import java.util.*;

public class Pedido {
    private int id;
    private int restauranteId;
    private boolean paraLevar; 
    private EstadoPedido estado;
    private LocalDateTime dataHora;
    private List<LinhaPedido> linhas = new ArrayList<>();

    // Construtores

    public Pedido() {}

    // Lógica de Negócio

    public double calcularPrecoTotal() {
        return linhas.stream().mapToDouble(LinhaPedido::getPreco).sum();
    }

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRestauranteId() { return restauranteId; }
    public void setRestauranteId(int restauranteId) { this.restauranteId = restauranteId; }

    public boolean isParaLevar() { return paraLevar; }
    public void setParaLevar(boolean paraLevar) { this.paraLevar = paraLevar; }

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public List<LinhaPedido> getLinhas() { return linhas; }
    public void setLinhas(List<LinhaPedido> linhas) { this.linhas = linhas; }
    public void addLinha(LinhaPedido linha) { this.linhas.add(linha); }
    public void removeLinha(LinhaPedido linha) { this.linhas.remove(linha); }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", restauranteId=" + restauranteId +
                ", paraLevar=" + paraLevar +
                ", estado=" + estado +
                ", dataHora=" + dataHora +
                ", linhas=" + linhas +
                '}';
    }
}
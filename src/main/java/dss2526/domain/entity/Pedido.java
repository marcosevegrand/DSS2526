package dss2526.domain.entity;

import dss2526.domain.enumeration.EstadoPedido;

import java.time.LocalDateTime;
import java.util.*;

public class Pedido {
    private int id;
    private EstadoPedido estado;
    private LocalDateTime dataHora;
    private boolean paraLevar;
    private List<LinhaPedido> linhasPedido = new ArrayList<>();

    // Construtores

    public Pedido() {
        this.dataHora = LocalDateTime.now();
        this.estado = EstadoPedido.INICIADO;
    }

    public Pedido(boolean paraLevar) {
        this();
        this.paraLevar = paraLevar;
    }

    // Lógica simples

    public double calcularPrecoTotal() {
        double total = 0.0;
        for (LinhaPedido linha : linhasPedido) {
            total += linha.getPreco();
        }
        return total;
    }

    // Getters e Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public boolean isParaLevar() { return paraLevar; }
    public void setParaLevar(boolean paraLevar) { this.paraLevar = paraLevar; }

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public List<LinhaPedido> getLinhasPedido() { return linhasPedido; }
    public void setLinhasPedido(List<LinhaPedido> linhasPedido) { this.linhasPedido = linhasPedido; }
}

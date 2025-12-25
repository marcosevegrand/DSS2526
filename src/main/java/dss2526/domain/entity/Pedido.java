package dss2526.domain.entity;

import dss2526.domain.enumeration.EstadoPedido;
import java.time.LocalDateTime;
import java.util.*;

public class Pedido {
    private int id;
    private int restauranteId;
    private EstadoPedido estado;
    private LocalDateTime dataHora;
    private LocalDateTime horaEntrega; 
    private boolean paraLevar; 
    private List<LinhaPedido> linhasPedido = new ArrayList<>();

    // Construtores

    public Pedido() {
        this.dataHora = LocalDateTime.now();
        this.estado = EstadoPedido.INICIADO;
    }

    public Pedido(int restauranteId, boolean paraLevar) {
        this();
        this.restauranteId = restauranteId;
        this.paraLevar = paraLevar;
    }

    // Lógica de Negócio

    /**
     * Calcula o preço total somando todas as linhas.
     */
    public double calcularPrecoTotal() {
        return linhasPedido.stream()
                .mapToDouble(LinhaPedido::getPreco)
                .sum();
    }

    /**
     * Calcula o tempo de atendimento em minutos.
     */
    public long calcularTempoAtendimento() {
        if (horaEntrega == null) return 0;
        return java.time.Duration.between(dataHora, horaEntrega).toMinutes();
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

    public LocalDateTime getHoraEntrega() { return horaEntrega; }
    public void setHoraEntrega(LocalDateTime horaEntrega) { this.horaEntrega = horaEntrega; }

    public List<LinhaPedido> getLinhasPedido() { return linhasPedido; }
    public void setLinhasPedido(List<LinhaPedido> linhasPedido) { this.linhasPedido = linhasPedido; }
}
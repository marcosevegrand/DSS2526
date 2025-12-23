package pt.uminho.dss.restaurante.domain.entity;

import pt.uminho.dss.restaurante.domain.enumeration.EstadoPedido;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido implements Serializable {
    private Long id;
    private boolean paraLevar;
    private EstadoPedido estado;
    private LocalDateTime dataHora;
    private List<LinhaPedido> linhasPedido = new ArrayList<>();

    public Pedido() {
        this.dataHora = LocalDateTime.now();
        this.estado = EstadoPedido.INICIADO;
    }


    public BigDecimal calcularPrecoTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (LinhaPedido linha : linhasPedido) {
            total = total.add(linha.getPreco());
        }
        return total;
    }

    // Getters e Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean isParaLevar() { return paraLevar; }
    public void setParaLevar(boolean paraLevar) { this.paraLevar = paraLevar; }

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public List<LinhaPedido> getLinhasPedido() { return linhasPedido; }
    public void setLinhasPedido(List<LinhaPedido> linhasPedido) { this.linhasPedido = linhasPedido; }


}

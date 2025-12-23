package pt.uminho.dss.restaurante.domain.entity;

import pt.uminho.dss.restaurante.domain.enumeration.EstadoPedido;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido implements Serializable {
    private Long id;
    private Boolean takeaway;
    private EstadoPedido estado;
    private LocalDateTime dataHora;
    private BigDecimal total;
    private List<LinhaPedido> linhasPedido = new ArrayList<>();

    public Pedido() {
        this.dataHora = LocalDateTime.now();
        this.estado = EstadoPedido.INICIADO;
        this.total = BigDecimal.ZERO;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Boolean getIsTakeaway() { return takeaway; }
    public void setIsTakeaway(Boolean takeaway) { this.takeaway = takeaway; }
    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public List<LinhaPedido> getLinhasPedido() { return linhasPedido; }
    public void setLinhasPedido(List<LinhaPedido> linhasPedido) { this.linhasPedido = linhasPedido; }

    public void calcularTotal() {
        this.total = linhasPedido.stream()
                .map(l -> l.getPrecoUnitario().multiply(new BigDecimal(l.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
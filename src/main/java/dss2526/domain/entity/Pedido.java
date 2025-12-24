package dss2526.domain.entity;

import dss2526.domain.enumeration.EstadoPedido;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido implements Serializable {
    private Integer id;
    private boolean paraLevar;
    private EstadoPedido estado;
    private LocalDateTime dataHora;
    private List<LinhaPedido> linhasPedido = new ArrayList<>();
    private String notaGeral;


    public Pedido() {
        this.dataHora = LocalDateTime.now();
        this.estado = EstadoPedido.INICIADO;
        this.notaGeral = "";
    }


    public BigDecimal calcularPrecoTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (LinhaPedido linha : linhasPedido) {
            total = total.add(linha.getPreco());
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

    public String getNotaGeral() { return notaGeral; }
    public void setNotaGeral(String notaGeral) { this.notaGeral = notaGeral; }

}

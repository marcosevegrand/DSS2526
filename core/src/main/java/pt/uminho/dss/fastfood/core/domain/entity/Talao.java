package pt.uminho.dss.fastfood.core.domain.entity;

import java.time.LocalDateTime;
import pt.uminho.dss.fastfood.core.domain.entity.Pedido;

public class Talao {

    private int id;
    private int numero; // número visível para o cliente
    private LocalDateTime dataHora;
    private float valorPago;
    private int tempoEsperaEstimado; // em minutos
    private int idPedido;

    public Talao(
        int numero,
        LocalDateTime dataHora,
        float valorPago,
        int tempoEsperaEstimado,
        int idPedido
    ) {
        this.numero = numero;
        this.dataHora = dataHora;
        this.valorPago = valorPago;
        this.tempoEsperaEstimado = tempoEsperaEstimado;
        this.idPedido = idPedido;
    }

    // Construtor conveniente a partir de um Pedido
    public Talao(int numero, Pedido pedido) {
        this(
            numero,
            LocalDateTime.now(),
            pedido.getPrecoTotal(),
            pedido.getTempoEsperaEstimado(),
            pedido.getId()
        );
    }

    // Construtor vazio para ORM/JDBC
    protected Talao() {}

    // Getters e setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public float getValorPago() {
        return valorPago;
    }

    public void setValorPago(float valorPago) {
        this.valorPago = valorPago;
    }

    public int getTempoEsperaEstimado() {
        return tempoEsperaEstimado;
    }

    public void setTempoEsperaEstimado(int tempoEsperaEstimado) {
        this.tempoEsperaEstimado = tempoEsperaEstimado;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }
}

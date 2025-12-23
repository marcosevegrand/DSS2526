package pt.uminho.dss.fastfood.core.domain.entity;

import java.time.LocalDateTime;

public class Talao {

    private int id;                 // PK na BD
    private int numero;             // número visível para o cliente
    private LocalDateTime dataHora;
    private float valor;            // valor total do pedido
    private int tempoEsperaEstimado;
    private int idPedido;           // FK para Pedido
    private boolean pagoNaCaixa;    // true se foi pago na caixa, false se no terminal

    public Talao(int numero,
                 LocalDateTime dataHora,
                 float valor,
                 int tempoEsperaEstimado,
                 int idPedido,
                 boolean pagoNaCaixa) {
        this.numero = numero;
        this.dataHora = dataHora;
        this.valor = valor;
        this.tempoEsperaEstimado = tempoEsperaEstimado;
        this.idPedido = idPedido;
        this.pagoNaCaixa = pagoNaCaixa;
    }

    // Construtor conveniente a partir de um Pedido
    public Talao(int numero, Pedido pedido, boolean pagoNaCaixa) {
        this(numero,
             LocalDateTime.now(),
             pedido.getPrecoTotal(),
             pedido.getTempoEsperaEstimado(),
             pedido.getId(),
             pagoNaCaixa);
    }

    // Construtor vazio para ORM/JDBC
    protected Talao() {}

    // Getters / setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public float getValor() { return valor; }
    public void setValor(float valor) { this.valor = valor; }

    public int getTempoEsperaEstimado() { return tempoEsperaEstimado; }
    public void setTempoEsperaEstimado(int tempoEsperaEstimado) {
        this.tempoEsperaEstimado = tempoEsperaEstimado;
    }

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public boolean isPagoNaCaixa() { return pagoNaCaixa; }
    public void setPagoNaCaixa(boolean pagoNaCaixa) { this.pagoNaCaixa = pagoNaCaixa; }
}

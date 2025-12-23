package pt.uminho.dss.restaurante.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pt.uminho.dss.restaurante.domain.enumeration.EstadoPedido;
import pt.uminho.dss.restaurante.domain.enumeration.ModoConsumo;

public class Pedido {

    private int id;
    private ModoConsumo modoConsumo;
    private EstadoPedido estado;

    private LocalDateTime dataHoraCriacao;
    private LocalDateTime dataHoraPagamento;

    private List<LinhaPedido> linhas;

    private float precoTotal;
    private int tempoEsperaEstimado; // em minutos

    private int idTerminal;
    private int idFuncionario; // que abriu/acompanha o pedido

    // --------------------------------------------------
    // Construtores
    // --------------------------------------------------

    public Pedido(ModoConsumo modoConsumo, int idTerminal, int idFuncionario) {
        this.modoConsumo = modoConsumo;
        this.estado = EstadoPedido.EM_CONSTRUCAO;
        this.dataHoraCriacao = LocalDateTime.now();
        this.linhas = new ArrayList<>();
        this.idTerminal = idTerminal;
        this.idFuncionario = idFuncionario;
        this.precoTotal = 0f;
        this.tempoEsperaEstimado = 0;
    }

    // Construtor vazio para ORM, se precisares
    protected Pedido() {}

    // --------------------------------------------------
    // Getters (e setters mínimos, se precisares)
    // --------------------------------------------------

    public int getId() {
        return id;
    }

    public ModoConsumo getModoConsumo() {
        return modoConsumo;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public LocalDateTime getDataHoraCriacao() {
        return dataHoraCriacao;
    }

    public LocalDateTime getDataHoraPagamento() {
        return dataHoraPagamento;
    }

    public List<LinhaPedido> getLinhas() {
        return Collections.unmodifiableList(linhas);
    }

    public float getPrecoTotal() {
        return precoTotal;
    }

    public int getTempoEsperaEstimado() {
        return tempoEsperaEstimado;
    }

    public int getIdTerminal() {
        return idTerminal;
    }

    public int getIdFuncionario() {
        return idFuncionario;
    }
}

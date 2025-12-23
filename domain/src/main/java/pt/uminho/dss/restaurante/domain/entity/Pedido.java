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

    // Construtor vazio para ORM
    protected Pedido() {}

    // --------------------------------------------------
    // MÉTODOS DE NEGÓCIO (FALTAVAM)
    // --------------------------------------------------

    /**
     * Adiciona linha ao pedido e actualiza total.
     */
    public void addLinha(LinhaPedido linha) {
        linhas.add(linha);
        actualizarPrecoTotal();
    }

    /**
     * Remove linhas por ID de item e quantidade.
     * Usa métodos da LinhaPedido.
     */
    public void removeLinhaPorItem(int idItem, int quantidade) {
        linhas.removeIf(linha -> {
            if (linha.getItemId() == idItem && linha.podeRemover(quantidade)) {
                linha.decrementarQuantidade(quantidade);
                return linha.getQuantidade() == 0;
            }
            return false;
        });
        actualizarPrecoTotal();
    }

    /**
     * Actualiza precoTotal somando subtotais das linhas.
     */
    private void actualizarPrecoTotal() {
        this.precoTotal = (float) linhas.stream()
            .mapToDouble(LinhaPedido::getSubtotal)
            .sum();
    }

    /**
     * Marca como pago (para VendaFacade).
     */
    public void pagar() {
        if (this.estado == EstadoPedido.EM_CONSTRUCAO) {
            this.estado = EstadoPedido.PAGO;
            this.dataHoraPagamento = LocalDateTime.now();
        }
    }

    // --------------------------------------------------
    // Getters e Setters
    // --------------------------------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ModoConsumo getModoConsumo() {
        return modoConsumo;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public LocalDateTime getDataHoraCriacao() {
        return dataHoraCriacao;
    }

    public LocalDateTime getDataHoraPagamento() {
        return dataHoraPagamento;
    }

    public void setDataHoraPagamento(LocalDateTime dataHoraPagamento) {
        this.dataHoraPagamento = dataHoraPagamento;
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

    public void setTempoEsperaEstimado(int tempoEsperaEstimado) {
        this.tempoEsperaEstimado = tempoEsperaEstimado;
    }

    public int getIdTerminal() {
        return idTerminal;
    }

    public int getIdFuncionario() {
        return idFuncionario;
    }
}

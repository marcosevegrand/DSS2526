package pt.uminho.dss.fastfood.core.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pt.uminho.dss.fastfood.core.domain.enumeration.EstadoPedido;
import pt.uminho.dss.fastfood.core.domain.enumeration.ModoConsumo;
import pt.uminho.dss.fastfood.core.domain.contract.Item;

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
    // Métodos de negócio
    // --------------------------------------------------

    public void adicionarLinha(
        Item item,
        int quantidade,
        String personalizacao
    ) {
        validarEstadoEditavel();
        LinhaPedido linha = new LinhaPedido(item, quantidade, personalizacao);
        linhas.add(linha);
        recalcularTotais();
    }

    public void removerLinha(int idLinha) {
        validarEstadoEditavel();
        linhas.removeIf(l -> l.getId() == idLinha);
        recalcularTotais();
    }

    public void editarLinha(
        int idLinha,
        int novaQuantidade,
        String novaPersonalizacao
    ) {
        validarEstadoEditavel();
        for (LinhaPedido l : linhas) {
            if (l.getId() == idLinha) {
                l.setQuantidade(novaQuantidade);
                l.setPersonalizacao(novaPersonalizacao);
                l.recalcularTotais();
                break;
            }
        }
        recalcularTotais();
    }

    public void confirmar() {
        if (linhas.isEmpty()) {
            throw new IllegalStateException(
                "Não é possível confirmar um pedido vazio."
            );
        }
        validarEstadoEditavel();
        this.estado = EstadoPedido.AGUARDA_PAGAMENTO;
    }

    public void marcarComoPago() {
        if (estado != EstadoPedido.AGUARDA_PAGAMENTO) {
            throw new IllegalStateException(
                "Pedido não está a aguardar pagamento."
            );
        }
        this.estado = EstadoPedido.PAGO;
        this.dataHoraPagamento = LocalDateTime.now();
        // Aqui poderás alterar para EM_ESPERA_PRODUCAO noutra camada
    }

    public void cancelar() {
        if (estado == EstadoPedido.PAGO) {
            throw new IllegalStateException(
                "Não é possível cancelar um pedido já pago."
            );
        }
        this.estado = EstadoPedido.CANCELADO;
    }

    private void validarEstadoEditavel() {
        if (estado != EstadoPedido.EM_CONSTRUCAO) {
            throw new IllegalStateException(
                "Pedido não pode ser alterado no estado atual: " + estado
            );
        }
    }

    private void recalcularTotais() {
        float total = 0f;
        int tempo = 0;
        for (LinhaPedido l : linhas) {
            total += l.getPrecoLinha();
            tempo = Math.max(tempo, l.getTempoPreparacao()); // pode ser soma se preferires
        }
        this.precoTotal = total;
        this.tempoEsperaEstimado = tempo;
    }

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

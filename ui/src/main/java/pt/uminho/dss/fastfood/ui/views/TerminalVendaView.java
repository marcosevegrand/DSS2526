package pt.uminho.dss.fastfood.ui.views;

import pt.uminho.dss.fastfood.core.domain.entity.Pedido;
import pt.uminho.dss.fastfood.core.domain.entity.Talao;
import pt.uminho.dss.fastfood.core.domain.enumeration.ModoConsumo;
import pt.uminho.dss.fastfood.venda.IVenda;

public class TerminalVendaView {

    private final IVenda venda;
    private final int idTerminal;

    private Pedido pedidoAtual;

    public TerminalVendaView(IVenda venda, int idTerminal) {
        this.venda = venda;
        this.idTerminal = idTerminal;
    }

    // =====================================================
    // 1. Entrada do cliente
    // =====================================================

    public void mostrarEcraInicial() {
        // Aqui desenhas o ecrã inicial (UI concreta: JavaFX, Swing, web, etc.)
        // Botões: "Comer no local" e "Levar".
        // Cada botão chama iniciarPedidoComerNoLocal() ou iniciarPedidoLevar().
    }

    public void iniciarPedidoComerNoLocal() {
        iniciarPedido(ModoConsumo.LOCAL);
    }

    public void iniciarPedidoLevar() {
        iniciarPedido(ModoConsumo.TAKE_AWAY);
    }

    private void iniciarPedido(ModoConsumo modo) {
        this.pedidoAtual = venda.iniciarPedido(modo, idTerminal, 0);
        mostrarEcraCatalogo();
    }

    // =====================================================
    // 2. Catálogo / Construção do pedido
    // =====================================================

    public void mostrarEcraCatalogo() {
        // Renderiza a lista de produtos/menus (vêm de outro serviço, ex.: CatalogoService).
        // A UI chama adicionarItem(...) ou removerItem(...) de acordo com as ações do cliente.
        actualizarResumoNaUI();
    }

    public void adicionarItem(
        int idProdutoOuMenu,
        String personalizacao,
        int quantidade
    ) {
        garantirPedidoAtual();
        this.pedidoAtual = venda.adicionarItem(
            pedidoAtual.getId(),
            idProdutoOuMenu,
            personalizacao,
            quantidade
        );
        actualizarResumoNaUI();
    }

    public void removerItem(int idLinha) {
        garantirPedidoAtual();
        this.pedidoAtual = venda.removerItem(pedidoAtual.getId(), idLinha);
        actualizarResumoNaUI();
    }

    public void editarItem(
        int idLinha,
        int novaQuantidade,
        String novaPersonalizacao
    ) {
        garantirPedidoAtual();
        this.pedidoAtual = venda.editarItem(
            pedidoAtual.getId(),
            idLinha,
            novaPersonalizacao,
            novaQuantidade
        );
        actualizarResumoNaUI();
    }

    private void actualizarResumoNaUI() {
        if (pedidoAtual == null) {
            return;
        }
        float total = pedidoAtual.getPrecoTotal();
        int tempo = pedidoAtual.getTempoEsperaEstimado();
        // Atualiza labels/elementos gráficos com total e tempo.
        // Ex.: lblTotal.setText(String.format("%.2f €", total));
        //      lblTempo.setText(tempo + " min");
    }

    // =====================================================
    // 3. Confirmação / Cancelamento
    // =====================================================

    public void onClickConfirmar() {
        garantirPedidoAtual();
        this.pedidoAtual = venda.confirmarPedido(pedidoAtual.getId());
        mostrarEcraPagamento();
    }

    public void onClickCancelar() {
        if (pedidoAtual != null) {
            venda.cancelarPedido(pedidoAtual.getId());
            this.pedidoAtual = null;
        }
        mostrarEcraInicial();
    }

    // =====================================================
    // 4. Pagamento
    // =====================================================

    public void mostrarEcraPagamento() {
        garantirPedidoAtual();
        // Renderiza ecrã com opções de pagamento (cartão, MBWay, etc.).
        // Ao submeter, chama finalizarPagamento(dadosPagamento).
    }

    public void finalizarPagamento(DadosPagamentoDTO dados) {
        garantirPedidoAtual();
        PagamentoDTO resultado = venda.pagar(pedidoAtual.getId(), dados);

        if (resultado.sucesso()) {
            mostrarEcraFinal();
        } else {
            // Mostra mensagem de erro e permite tentar de novo ou cancelar.
        }
    }

    // =====================================================
    // 5. Ecrã final
    // =====================================================

    public void mostrarEcraFinal() {
        garantirPedidoAtual();
        Talao talao = venda.emitirTalao(pedidoAtual.getId());

        // Mostra número do pedido, valor pago, tempo de espera, etc.
        // Ex.: lblNumero.setText("Pedido nº " + talao.getNumero());

        // Após algum tempo ou botão "Concluir":
        this.pedidoAtual = null;
        mostrarEcraInicial();
    }

    // =====================================================
    // Utilitário interno
    // =====================================================

    private void garantirPedidoAtual() {
        if (pedidoAtual == null) {
            throw new IllegalStateException(
                "Não existe pedido em curso no terminal " + idTerminal
            );
        }
    }
}

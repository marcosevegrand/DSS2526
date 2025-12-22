package pt.uminho.dss.fastfood.ui.controllers;

import pt.uminho.dss.fastfood.venda.IVenda;
import pt.uminho.dss.fastfood.core.domain.Pedido;
import pt.uminho.dss.fastfood.core.domain.enums.ModoConsumo;
import pt.uminho.dss.fastfood.core.domain.dto.DadosPagamentoDTO;
import pt.uminho.dss.fastfood.core.domain.dto.PagamentoDTO;

public class VendaController {

    private final IVenda vendaService;
    private Pedido pedidoAtual;

    private final int idTerminal;
    private final Integer idFuncionario; // pode ser null se não houver

    public VendaController(IVenda vendaService, int idTerminal, Integer idFuncionario) {
        this.vendaService = vendaService;
        this.idTerminal = idTerminal;
        this.idFuncionario = idFuncionario;
    }

    // 1. Ciclo de vida do pedido

    public void iniciarPedido(ModoConsumo modo) {
        this.pedidoAtual = vendaService.iniciarPedido(modo, idTerminal,
                idFuncionario != null ? idFuncionario : 0);
    }

    public Pedido getPedidoAtual() {
        return pedidoAtual;
    }

    public void adicionarItem(int idProdutoOuMenu, String personalizacao, int quantidade) {
        garantirPedidoAtual();
        this.pedidoAtual = vendaService.adicionarItem(
                pedidoAtual.getId(),
                idProdutoOuMenu,
                personalizacao,
                quantidade
        );
    }

    public void removerItem(int idLinhaPedido) {
        garantirPedidoAtual();
        this.pedidoAtual = vendaService.removerItem(
                pedidoAtual.getId(),
                idLinhaPedido
        );
    }

    public void editarItem(int idLinhaPedido, int novaQuantidade, String novaPersonalizacao) {
        garantirPedidoAtual();
        this.pedidoAtual = vendaService.editarItem(
                pedidoAtual.getId(),
                idLinhaPedido,
                novaPersonalizacao,
                novaQuantidade
        );
    }

    public void confirmarPedido() {
        garantirPedidoAtual();
        this.pedidoAtual = vendaService.confirmarPedido(pedidoAtual.getId());
    }

    public void cancelarPedido() {
        if (pedidoAtual == null) {
            return;
        }
        vendaService.cancelarPedido(pedidoAtual.getId());
        this.pedidoAtual = null;
    }

    // 2. Pagamento

    public PagamentoDTO pagar(DadosPagamentoDTO dadosPagamento) {
        garantirPedidoAtual();
        PagamentoDTO resultado = vendaService.pagar(pedidoAtual.getId(), dadosPagamento);
        if (resultado.sucesso()) {
            // opcionalmente pode-se já pedir o talão aqui
            // Talao talao = vendaService.emitirTalao(pedidoAtual.getId());
        }
        return resultado;
    }

    // 3. Utilitário interno

    private void garantirPedidoAtual() {
        if (pedidoAtual == null) {
            throw new IllegalStateException("Não existe pedido em curso.");
        }
    }
}

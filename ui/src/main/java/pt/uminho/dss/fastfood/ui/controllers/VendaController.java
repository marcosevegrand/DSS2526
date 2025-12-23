package pt.uminho.dss.fastfood.ui.controllers;

import pt.uminho.dss.fastfood.core.domain.entity.Pedido;
import pt.uminho.dss.fastfood.core.domain.enumeration.ModoConsumo;
import pt.uminho.dss.fastfood.venda.IVenda;

public class VendaController {

    private final IVenda vendaService;
    private Pedido pedidoAtual;

    private final int idTerminal;
    private final Integer idFuncionario; // pode ser null se não houver

    public VendaController(IVenda vendaService,
                           int idTerminal,
                           Integer idFuncionario) {
        this.vendaService = vendaService;
        this.idTerminal = idTerminal;
        this.idFuncionario = idFuncionario;
    }

    // 1. Ciclo de vida do pedido

    public void iniciarPedido(ModoConsumo modo) {
        this.pedidoAtual = vendaService.iniciarPedido(
                modo,
                idTerminal,
                idFuncionario != null ? idFuncionario : 0
        );
    }

    public Pedido getPedidoAtual() {
        return pedidoAtual;
    }

    public void adicionarItem(int idProdutoOuMenu,
                              String personalizacao,
                              int quantidade) {
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

    public void editarItem(int idLinhaPedido,
                           int novaQuantidade,
                           String novaPersonalizacao) {
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

    // 3. Utilitário interno

    private void garantirPedidoAtual() {
        if (pedidoAtual == null) {
            throw new IllegalStateException("Não existe pedido em curso.");
        }
    }
}

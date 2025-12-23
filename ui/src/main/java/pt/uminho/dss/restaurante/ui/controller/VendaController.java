// ...existing code...
package pt.uminho.dss.restaurante.ui.controller;

import java.util.List;
import java.util.Objects;

import pt.uminho.dss.restaurante.venda.IVenda;
import pt.uminho.dss.restaurante.domain.entity.Pedido;
import pt.uminho.dss.restaurante.domain.entity.Produto;
import pt.uminho.dss.restaurante.domain.entity.Menu;
import pt.uminho.dss.restaurante.domain.enumeration.ModoConsumo;

/**
 * Controller que expõe operações úteis para a UI do terminal de vendas.
 * Encapsula IVenda e fornece utilitários (ex.: obter id por índice).
 */
public class VendaController {

    private final IVenda venda;

    public VendaController(IVenda venda) {
        this.venda = Objects.requireNonNull(venda);
    }

    public Pedido novoPedido(ModoConsumo modo, int idTerminal, int idFuncionario) {
        return venda.criarPedido(modo, idTerminal, idFuncionario);
    }

    public List<Produto> listarProdutos() {
        return venda.listarProdutos();
    }

    public List<Menu> listarMenus() {
        return venda.listarMenus();
    }

    public void adicionarItem(int idPedido, int idItem, int quantidade) {
        venda.adicionarItem(idPedido, idItem, quantidade);
    }

    public void removerItem(int idPedido, int idItem, int quantidade) {
        venda.removerItem(idPedido, idItem, quantidade);
    }

    public void pagarPedido(int idPedido) {
        venda.pagarPedido(idPedido);
    }

    public void cancelarPedido(int idPedido) {
        venda.cancelarPedido(idPedido);
    }

    public Pedido obterPedido(int idPedido) {
        return venda.obterPedido(idPedido);
    }

    /**
     * Utilitários para mapear índice da lista para id do item.
     * Retornam -1 se índice inválido.
     */
    public int getProdutoIdByIndex(int index) {
        List<Produto> produtos = listarProdutos();
        if (index >= 0 && index < produtos.size()) {
            Integer id = produtos.get(index).getId();
            return id != null ? id : -1;
        }
        return -1;
    }

    public int getMenuIdByIndex(int index) {
        List<Menu> menus = listarMenus();
        if (index >= 0 && index < menus.size()) {
            Integer id = menus.get(index).getId();
            return id != null ? id : -1;
        }
        return -1;
    }
}
// ...existing code...
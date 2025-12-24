package dss2526.venda;

import dss2526.domain.entity.*;
import dss2526.domain.contract.Item;
import dss2526.domain.enumeration.EstadoPedido;
import dss2526.data.contract.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VendaFacade {

    private final ProdutoDAO produtoDAO;
    private final MenuDAO menuDAO;
    private final PedidoDAO pedidoDAO;

    public VendaFacade(ProdutoDAO produtoDAO, MenuDAO menuDAO, PedidoDAO pedidoDAO) {
        this.produtoDAO = produtoDAO;
        this.menuDAO = menuDAO;
        this.pedidoDAO = pedidoDAO;
    }

    @Override
    public Pedido criarPedido(boolean paraLevar) {
        Pedido pedido = new Pedido();
        pedido.setParaLevar(paraLevar);
        pedido.setEstado(EstadoPedido.INICIADO);
        pedidoDAO.put(pedido);
        return pedido;
    }

    @Override
    public void adicionarItem(int idPedido, int idItem, int quantidade, String observacao) {
        Pedido pedido = findPedidoOrThrow(idPedido);

        // Tenta encontrar como Produto
        Optional<Produto> optProduto = produtoDAO.findById(idItem);
        if (optProduto.isPresent()) {
            Produto produto = optProduto.get();
            produto.validarDisponibilidade();
            // Passamos a observação para o construtor da LinhaPedido
            LinhaPedido linha = new LinhaPedido(produto, quantidade, produto.getPreco(), observacao);
            pedido.getLinhasPedido().add(linha);
            pedidoDAO.update(pedido);
            return;
        }

        // Tenta encontrar como Menu
        Optional<Menu> optMenu = menuDAO.findById(idItem);
        if (optMenu.isPresent()) {
            Menu menu = optMenu.get();
            menu.validarDisponibilidade();
            // Passamos a observação para o construtor da LinhaPedido
            LinhaPedido linha = new LinhaPedido(menu, quantidade, menu.getPreco(), observacao);
            pedido.getLinhasPedido().add(linha);
            pedidoDAO.update(pedido);
            return;
        }

        throw new IllegalArgumentException("Item não encontrado: ID " + idItem);
    }

    @Override
    public void removerItem(int idPedido, int idItem, int quantidade) {
        Pedido pedido = findPedidoOrThrow(idPedido);
        List<LinhaPedido> linhas = pedido.getLinhasPedido();
        LinhaPedido linhaEncontrada = linhas.stream()
                .filter(l -> l.getId() == idItem) 
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado no pedido"));

        if (linhaEncontrada.getQuantidade() <= quantidade) {
            linhas.remove(linhaEncontrada);
        } else {
            linhaEncontrada.setQuantidade(linhaEncontrada.getQuantidade() - quantidade);
        }
        pedidoDAO.update(pedido);
    }

    @Override
    public void adicionarNota(int idPedido, String nota) {
        Pedido pedido = findPedidoOrThrow(idPedido);
        pedido.setNotaGeral(nota);
        pedidoDAO.update(pedido);
    }

    @Override
    public void confirmarPedido(int idPedido) {
        Pedido pedido = findPedidoOrThrow(idPedido);
        pedido.setEstado(EstadoPedido.CONFIRMADO);
        pedidoDAO.put(pedido);
    }

    @Override
    public void cancelarPedido(int idPedido) {
        Pedido pedido = findPedidoOrThrow(idPedido);
        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoDAO.update(pedido);
    }

    @Override
    public Pedido obterPedido(int idPedido) {
        return findPedidoOrThrow(idPedido);
    }

    @Override
    public List<Item> obterItemsDisponiveis() {
        List<Item> items = new ArrayList<>();
        items.addAll(produtoDAO.findAll());
        items.addAll(menuDAO.findAll());
        return items.stream()
                .filter(Item::isDisponivel)
                .toList();
    }

    private Pedido findPedidoOrThrow(int idPedido) {
        return pedidoDAO.findById(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: ID " + idPedido));
    }
}

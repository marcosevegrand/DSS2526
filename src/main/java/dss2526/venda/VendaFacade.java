package dss2526.venda;

import dss2526.data.contract.PedidoDAO;
import dss2526.data.contract.ProdutoDAO;
import dss2526.data.contract.MenuDAO;
import dss2526.domain.entity.*;
import dss2526.domain.entity.LinhaPedido;
import dss2526.domain.contract.Item;
import dss2526.domain.enumeration.EstadoPedido;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VendaFacade implements IVendaFacade {

    private final PedidoDAO pedidoDAO;
    private final ProdutoDAO produtoDAO;
    private final MenuDAO menuDAO;

    public VendaFacade(PedidoDAO pedidoDAO, ProdutoDAO produtoDAO, MenuDAO menuDAO) {
        this.pedidoDAO = pedidoDAO;
        this.produtoDAO = produtoDAO;
        this.menuDAO = menuDAO;
    }

    @Override
    public Pedido criarPedido(boolean paraLevar) {
        Pedido pedido = new Pedido();
        pedido.setParaLevar(paraLevar);
        pedido.setEstado(EstadoPedido.INICIADO);
        
        // No teu DAO de Ingredientes, o put recebe (Key, Value)
        // Se o ID for gerado pelo banco (Auto-increment), o ID virá nulo aqui 
        // e o DAO tratará. Se for manual, precisas de gerar um ID.
        pedidoDAO.put(pedido.getId(), pedido); 
        return pedido;
    }

    @Override
    public void adicionarItem(int idPedido, int idItem, int quantidade, String observacao) {
        Pedido pedido = obterPedidoOuErro(idPedido);

        // 1. Tenta buscar como Produto no ProdutoDAO (usando .get como no teu exemplo)
        Produto produto = produtoDAO.get(idItem);
        if (produto != null) {
            LinhaPedido linha = new LinhaPedido(produto, quantidade, produto.getPreco(), observacao);
            pedido.getLinhasPedido().add(linha);
            pedidoDAO.put(pedido.getId(), pedido); // Atualiza o pedido com a nova linha
            return;
        }

        // 2. Tenta buscar como Menu
        Menu menu = menuDAO.get(idItem);
        if (menu != null) {
            LinhaPedido linha = new LinhaPedido(menu, quantidade, menu.getPreco(), observacao);
            pedido.getLinhasPedido().add(linha);
            pedidoDAO.put(pedido.getId(), pedido);
            return;
        }

        throw new IllegalArgumentException("Item não encontrado: ID " + idItem);
    }

    @Override
    public void removerItem(int idPedido, int idItem, int quantidade) {
        Pedido pedido = obterPedidoOuErro(idPedido);
        List<LinhaPedido> linhas = pedido.getLinhasPedido();

        // Encontra a linha que contém o item com o ID pretendido
        LinhaPedido linhaEncontrada = linhas.stream()
                .filter(l -> l.getItem().getId().equals(idItem))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item não está no pedido"));

        if (linhaEncontrada.getQuantidade() <= quantidade) {
            linhas.remove(linhaEncontrada);
        } else {
            linhaEncontrada.setQuantidade(linhaEncontrada.getQuantidade() - quantidade);
        }

        pedidoDAO.put(pedido.getId(), pedido); // Sincroniza com a base de dados
    }

    @Override
    public void adicionarNota(int idPedido, String nota) {
        Pedido pedido = obterPedidoOuErro(idPedido);
        pedido.setNotaGeral(nota);
        pedidoDAO.put(pedido.getId(), pedido);
    }

    @Override
    public void confirmarPedido(int idPedido) {
        Pedido pedido = obterPedidoOuErro(idPedido);
        pedido.setEstado(EstadoPedido.CONFIRMADO);
        pedidoDAO.put(pedido.getId(), pedido);
    }

    @Override
    public void cancelarPedido(int idPedido) {
        Pedido pedido = obterPedidoOuErro(idPedido);
        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoDAO.put(pedido.getId(), pedido);
    }

    @Override
    public Pedido obterPedido(int idPedido) {
        return obterPedidoOuErro(idPedido);
    }

    @Override
    public List<Item> obterItemsDisponiveis() {
        List<Item> items = new ArrayList<>();
        // Usa o método .values() que tens no teu DAO de exemplo
        items.addAll(produtoDAO.values());
        items.addAll(menuDAO.values());
        
        return items.stream()
                .filter(Item::isDisponivel)
                .toList();
    }

    // Método auxiliar para evitar repetição de código
    private Pedido obterPedidoOuErro(int idPedido) {
        Pedido p = pedidoDAO.get(idPedido);
        if (p == null) {
            throw new IllegalArgumentException("Pedido não encontrado: ID " + idPedido);
        }
        return p;
    }
}
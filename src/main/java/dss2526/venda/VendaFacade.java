package dss2526.venda;

import dss2526.data.contract.PedidoDAO;
import dss2526.data.contract.ProdutoDAO;
import dss2526.data.contract.MenuDAO;
import dss2526.domain.entity.*;
import dss2526.domain.contract.Item;
import dss2526.domain.enumeration.EstadoPedido;
import dss2526.producao.IProducaoFacade; 

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VendaFacade implements IVendaFacade {

    private final PedidoDAO pedidoDAO;
    private final ProdutoDAO produtoDAO;
    private final MenuDAO menuDAO;
    private final IProducaoFacade producaoFacade; 

    public VendaFacade(PedidoDAO pedidoDAO, 
                       ProdutoDAO produtoDAO, 
                       MenuDAO menuDAO, 
                       IProducaoFacade producaoFacade) {
        this.pedidoDAO = pedidoDAO;
        this.produtoDAO = produtoDAO;
        this.menuDAO = menuDAO;
        this.producaoFacade = producaoFacade;
    }

    @Override
    public Pedido criarPedido(int restauranteId, boolean paraLevar) {
        Pedido pedido = new Pedido(restauranteId, paraLevar);
        pedidoDAO.save(pedido); 
        return pedido;
    }

    @Override
    public void adicionarItem(int idPedido, int idItem, int quantidade, String observacao) {
        Pedido pedido = obterPedidoOuErro(idPedido);
        Item item = encontrarItemNoCatalogo(idItem);
        
        LinhaPedido linha = new LinhaPedido(item, quantidade, item.getPreco(), observacao);
        pedido.getLinhasPedido().add(linha);
        
        pedidoDAO.update(pedido);
    }

    @Override
    public void confirmarPedido(int idPedido) {
        Pedido pedido = obterPedidoOuErro(idPedido);
        
        if (pedido.getLinhasPedido().isEmpty()) {
            throw new IllegalStateException("Não é possível confirmar um pedido vazio.");
        }

        pedido.setEstado(EstadoPedido.CONFIRMADO);
        pedidoDAO.update(pedido);

        if (producaoFacade != null) {
            producaoFacade.registarNovoPedido(pedido);
        }
    }

    @Override
    public void removerItem(int idPedido, int idItem, int quantidade) {
        Pedido pedido = obterPedidoOuErro(idPedido);
        
        pedido.getLinhasPedido().removeIf(l -> {
            if (l.getItem().getId() == idItem) {
                if (l.getQuantidade() <= quantidade) return true;
                l.setQuantidade(l.getQuantidade() - quantidade);
            }
            return false;
        });

        pedidoDAO.update(pedido);
    }

    @Override
    public void adicionarNota(int idPedido, String nota) {
        Pedido pedido = obterPedidoOuErro(idPedido);
        // Supondo que a classe Pedido tem o campo notaGeral ou similar
        // Se não tiver, podes adicionar à tua entidade Pedido
        // pedido.setNotaGeral(nota); 
        pedidoDAO.update(pedido);
    }

    @Override
    public List<Item> obterItemsDisponiveis() {
        List<Item> catalogo = new ArrayList<>();
        catalogo.addAll(produtoDAO.findAll());
        catalogo.addAll(menuDAO.findAll());
        
        return catalogo.stream()
                .filter(Item::isDisponivel)
                .collect(Collectors.toList());
    }

    @Override
    public Pedido obterPedido(int idPedido) {
        return obterPedidoOuErro(idPedido);
    }

    @Override
    public void cancelarPedido(int idPedido) {
        Pedido pedido = obterPedidoOuErro(idPedido);
        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoDAO.update(pedido);
    }

    private Pedido obterPedidoOuErro(int idPedido) {
        Pedido p = pedidoDAO.findById(idPedido);
        if (p == null) throw new IllegalArgumentException("Pedido #" + idPedido + " não existe.");
        return p;
    }

    private Item encontrarItemNoCatalogo(int idItem) {
        Produto p = produtoDAO.findById(idItem);
        if (p != null) return p;
        
        Menu m = menuDAO.findById(idItem);
        if (m != null) return m;
        
        throw new IllegalArgumentException("Item #" + idItem + " não encontrado.");
    }
}
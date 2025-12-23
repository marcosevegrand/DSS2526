package pt.uminho.dss.restaurante.venda;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import pt.uminho.dss.restaurante.domain.entity.Pedido;
import pt.uminho.dss.restaurante.domain.entity.Produto;
import pt.uminho.dss.restaurante.domain.entity.Menu;
import pt.uminho.dss.restaurante.domain.entity.LinhaPedido;
import pt.uminho.dss.restaurante.domain.enumeration.EstadoPedido;
import pt.uminho.dss.restaurante.persistence.contract.ProdutoDAO;
import pt.uminho.dss.restaurante.persistence.contract.MenuDAO;
import pt.uminho.dss.restaurante.persistence.contract.PedidoDAO;

/**
 * Fachada de venda — versão que usa Optional no findById e métodos de Pedido addLinha/removeLinhaPorItem.
 */
public class VendaFacade {

    private final ProdutoDAO produtoDAO;
    private final MenuDAO menuDAO;
    private final PedidoDAO pedidoDAO;

    public VendaFacade(ProdutoDAO produtoDAO, MenuDAO menuDAO, PedidoDAO pedidoDAO) {
        this.produtoDAO = Objects.requireNonNull(produtoDAO);
        this.menuDAO = Objects.requireNonNull(menuDAO);
        this.pedidoDAO = Objects.requireNonNull(pedidoDAO);
    }

    public Pedido criarPedido(Boolean takeaway, int idTerminal, int idFuncionario) {
        Pedido p = new Pedido(takeaway, idTerminal, idFuncionario);
        pedidoDAO.save(p);
        return p;
    }

    private Pedido findPedidoOrThrow(int idPedido) {
        Optional<Pedido> opt = pedidoDAO.findById(idPedido);
        return opt.orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + idPedido));
    }

    @Override
    public void adicionarItem(int idPedido, int idItem, int quantidade) {
        Pedido pedido = findPedidoOrThrow(idPedido);

        Optional<Produto> optProduto = produtoDAO.findById(idItem);
        if (optProduto.isPresent()) {
            Produto produto = optProduto.get();
            LinhaPedido linha = new LinhaPedido(produto, quantidade);
            pedido.addLinha(linha);
            pedidoDAO.update(pedido);
            return;
        }

        Optional<Menu> optMenu = menuDAO.findById(idItem);
        if (optMenu.isPresent()) {
            Menu menu = optMenu.get();
            LinhaPedido linha = new LinhaPedido(menu, quantidade);
            pedido.addLinha(linha);
            pedidoDAO.update(pedido);
            return;
        }

        throw new IllegalArgumentException("Item não encontrado: ID " + idItem);
    }

    @Override
    public void removerItem(int idPedido, int idItem, int quantidade) {
        Pedido pedido = findPedidoOrThrow(idPedido);
        pedido.removeLinhaPorItem(idItem, quantidade);  // teu método no Pedido
        pedidoDAO.update(pedido);
    }

    @Override
    public void pagarPedido(int idPedido) {
        Pedido pedido = findPedidoOrThrow(idPedido);
        if (pedido.getEstado() == EstadoPedido.EM_CONSTRUCAO) {
            pedido.setEstado(EstadoPedido.PAGO);
            pedidoDAO.update(pedido);
        }
    }

    @Override
    public void cancelarPedido(int idPedido) {
        Pedido pedido = findPedidoOrThrow(idPedido);
        if (pedido.getEstado() == EstadoPedido.EM_CONSTRUCAO) {
            pedido.setEstado(EstadoPedido.CANCELADO);
            pedidoDAO.update(pedido);
        }
    }

    @Override
    public Pedido obterPedido(int idPedido) {
        return findPedidoOrThrow(idPedido);
    }

    @Override
    public List<Produto> listarProdutos() {
        return produtoDAO.findAll();
    }

    @Override
    public List<Menu> listarMenus() {
        return menuDAO.findAll();
    }
}

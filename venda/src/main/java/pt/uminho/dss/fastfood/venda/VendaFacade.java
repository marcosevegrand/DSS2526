package pt.uminho.dss.fastfood.venda;

import pt.uminho.dss.fastfood.core.domain.Menu;
import pt.uminho.dss.fastfood.core.domain.Pedido;
import pt.uminho.dss.fastfood.core.domain.Produto;
import pt.uminho.dss.fastfood.core.domain.enums.ModoConsumo;
import pt.uminho.dss.fastfood.persistence.MenuDAO;
import pt.uminho.dss.fastfood.persistence.PedidoDAO;
import pt.uminho.dss.fastfood.persistence.ProdutoDAO;

public class VendaFacade implements IVenda {

    private final PedidoDAO pedidoDAO;
    private final ProdutoDAO produtoDAO;
    private final MenuDAO menuDAO;

    public VendaFacade(PedidoDAO pedidoDAO,
                       ProdutoDAO produtoDAO,
                       MenuDAO menuDAO) {
        this.pedidoDAO = pedidoDAO;
        this.produtoDAO = produtoDAO;
        this.menuDAO = menuDAO;
    }

    // -------------------------------------------------
    // 1. Ciclo de vida do pedido
    // -------------------------------------------------

    @Override
    public Pedido iniciarPedido(ModoConsumo modoConsumo, int idTerminal, int idFuncionario) {
        Pedido p = new Pedido(modoConsumo, idTerminal, idFuncionario);
        pedidoDAO.save(p);
        return p;
    }

    @Override
    public Pedido adicionarItem(int idPedido, int idProdutoOuMenu, String personalizacao, int quantidade) {
        Pedido p = pedidoDAO.findById(idPedido);
        if (p == null) {
            throw new IllegalArgumentException("Pedido não encontrado: " + idPedido);
        }

        // 1º tenta como produto
        Produto produto = produtoDAO.findById(idProdutoOuMenu);
        if (produto != null) {
            p.adicionarLinha(produto, quantidade, personalizacao);
        } else {
            // se não for produto, tenta como menu
            Menu menu = menuDAO.findById(idProdutoOuMenu);
            if (menu == null) {
                throw new IllegalArgumentException("Produto/Menu não encontrado: " + idProdutoOuMenu);
            }
            p.adicionarLinha(menu, quantidade, personalizacao);
        }

        pedidoDAO.update(p);
        return p;
    }

    @Override
    public Pedido removerItem(int idPedido, int idLinhaPedido) {
        Pedido p = pedidoDAO.findById(idPedido);
        if (p == null) {
            throw new IllegalArgumentException("Pedido não encontrado: " + idPedido);
        }

        p.removerLinha(idLinhaPedido);
        pedidoDAO.update(p);
        return p;
    }

    @Override
    public Pedido editarItem(int idPedido,
                             int idLinhaPedido,
                             String novaPersonalizacao,
                             int novaQuantidade) {
        Pedido p = pedidoDAO.findById(idPedido);
        if (p == null) {
            throw new IllegalArgumentException("Pedido não encontrado: " + idPedido);
        }

        p.editarLinha(idLinhaPedido, novaQuantidade, novaPersonalizacao);
        pedidoDAO.update(p);
        return p;
    }

    @Override
    public Pedido cancelarPedido(int idPedido) {
        Pedido p = pedidoDAO.findById(idPedido);
        if (p == null) {
            throw new IllegalArgumentException("Pedido não encontrado: " + idPedido);
        }

        p.cancelar();
        pedidoDAO.update(p);
        return p;
    }

    @Override
    public Pedido confirmarPedido(int idPedido) {
        Pedido p = pedidoDAO.findById(idPedido);
        if (p == null) {
            throw new IllegalArgumentException("Pedido não encontrado: " + idPedido);
        }

        p.confirmar();
        pedidoDAO.update(p);
        return p;
    }

    @Override
    public Pedido obterPedido(int idPedido) {
        Pedido p = pedidoDAO.findById(idPedido);
        if (p == null) {
            throw new IllegalArgumentException("Pedido não encontrado: " + idPedido);
        }
        return p;
    }

    // -------------------------------------------------
    // 2. Pagamento e talão
    // -------------------------------------------------

    @Override
    public PagamentoDTO pagar(int idPedido, DadosPagamentoDTO dadosPagamento) {
        Pedido p = pedidoDAO.findById(idPedido);
        if (p == null) {
            throw new IllegalArgumentException("Pedido não encontrado: " + idPedido);
        }

        // chamar módulo de pagamento, validar, etc.
        boolean ok = processadorPagamento.pagar(p, dadosPagamento);

        if (ok) {
            p.marcarComoPago();
            pedidoDAO.update(p);
            return new PagamentoDTO(true, "Pagamento OK", p.getPrecoTotal());
        } else {
            return new PagamentoDTO(false, "Falha no pagamento", 0f);
        }
    }

    @Override
    public Talao emitirTalao(int idPedido) {
        Pedido p = obterPedido(idPedido);
        if (p.getEstado() != EstadoPedido.PAGO) {
            throw new IllegalStateException("Só é possível emitir talão para pedidos pagos.");
        }
        Talao t = new Talao(p);
        talaoDAO.save(t);
        return t;
    }
}

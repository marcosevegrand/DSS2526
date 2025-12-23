package pt.uminho.dss.fastfood.venda;

import pt.uminho.dss.restaurante.core.domain.entity.Menu;
import pt.uminho.dss.restaurante.core.domain.entity.Pedido;
import pt.uminho.dss.restaurante.core.domain.entity.Produto;
import pt.uminho.dss.restaurante.core.domain.entity.Talao;
import pt.uminho.dss.restaurante.core.domain.enumeration.EstadoPedido;
import pt.uminho.dss.restaurante.core.domain.enumeration.ModoConsumo;
import pt.uminho.dss.restaurante.persistence.contract.MenuDAO;
import pt.uminho.dss.restaurante.persistence.contract.PedidoDAO;
import pt.uminho.dss.restaurante.persistence.contract.ProdutoDAO;
import pt.uminho.dss.restaurante.persistence.contract.TalaoDAO;

public class VendaFacade implements IVenda {

    private final PedidoDAO pedidoDAO;
    private final ProdutoDAO produtoDAO;
    private final MenuDAO menuDAO;
    private final TalaoDAO talaoDAO;

    public VendaFacade(
        PedidoDAO pedidoDAO,
        ProdutoDAO produtoDAO,
        MenuDAO menuDAO,
        TalaoDAO talaoDAO
    ) {
        this.pedidoDAO = pedidoDAO;
        this.produtoDAO = produtoDAO;
        this.menuDAO = menuDAO;
        this.talaoDAO = talaoDAO;
    }

    // -------------------------------------------------
    // 1. Ciclo de vida do pedido
    // -------------------------------------------------

    @Override
    public Pedido iniciarPedido(
        ModoConsumo modoConsumo,
        int idTerminal,
        int idFuncionario
    ) {
        Pedido p = new Pedido(modoConsumo, idTerminal, idFuncionario);
        pedidoDAO.save(p);
        return p;
    }

    @Override
    public Pedido adicionarItem(
        int idPedido,
        int idProdutoOuMenu,
        String personalizacao,
        int quantidade
    ) {
        Pedido p = obterPedido(idPedido);

        // tenta como Produto
        Produto produto = produtoDAO.findById(idProdutoOuMenu);
        if (produto != null) {
            p.adicionarLinha(produto, quantidade, personalizacao);
        } else {
            // tenta como Menu
            Menu menu = menuDAO.findById(idProdutoOuMenu);
            if (menu == null) {
                throw new IllegalArgumentException(
                    "Produto/Menu não encontrado: " + idProdutoOuMenu
                );
            }
            p.adicionarLinha(menu, quantidade, personalizacao);
        }

        pedidoDAO.update(p);
        return p;
    }

    @Override
    public Pedido removerItem(int idPedido, int idLinhaPedido) {
        Pedido p = obterPedido(idPedido);
        p.removerLinha(idLinhaPedido);
        pedidoDAO.update(p);
        return p;
    }

    @Override
    public Pedido editarItem(
        int idPedido,
        int idLinhaPedido,
        String novaPersonalizacao,
        int novaQuantidade
    ) {
        Pedido p = obterPedido(idPedido);
        p.editarLinha(idLinhaPedido, novaQuantidade, novaPersonalizacao);
        pedidoDAO.update(p);
        return p;
    }

    @Override
    public void cancelarPedido(int idPedido) {
        Pedido p = obterPedido(idPedido);
        p.cancelar();
        pedidoDAO.update(p);
    }

    @Override
    public Pedido confirmarPedido(int idPedido) {
        Pedido p = obterPedido(idPedido);
        p.confirmar();
        pedidoDAO.update(p);
        return p;
    }

    public Pedido obterPedido(int idPedido) {
        Pedido p = pedidoDAO.findById(idPedido);
        if (p == null) {
            throw new IllegalArgumentException(
                "Pedido não encontrado: " + idPedido
            );
        }
        return p;
    }

    // -------------------------------------------------
    // 2. Pagamento simplificado
    // -------------------------------------------------

    @Override
    public Pedido marcarComoPagoNoTerminal(int idPedido) {
        Pedido p = obterPedido(idPedido);
        if (p.getEstado() != EstadoPedido.AGUARDA_PAGAMENTO) {
            throw new IllegalStateException(
                "Pedido não está a aguardar pagamento."
            );
        }
        p.marcarComoPago();
        pedidoDAO.update(p);
        return p;
    }

    @Override
    public Pedido marcarComoPagoNaCaixa(int idPedido) {
        Pedido p = obterPedido(idPedido);
        if (p.getEstado() != EstadoPedido.AGUARDA_PAGAMENTO) {
            throw new IllegalStateException(
                "Pedido não está a aguardar pagamento."
            );
        }
        p.marcarComoPago();
        pedidoDAO.update(p);
        return p;
    }

    @Override
    public Talao emitirTalao(int idPedido) {
        Pedido p = obterPedido(idPedido);
        if (p.getEstado() != EstadoPedido.PAGO) {
            throw new IllegalStateException(
                "Só é possível emitir talão para pedidos pagos."
            );
        }

        // estratégia simples para gerar número de talão
        int numeroTalao = p.getId(); // por enquanto, usa o id do pedido
        boolean pagoNaCaixa = false; // ajusta conforme o fluxo onde chamas

        Talao t = new Talao(numeroTalao, p, pagoNaCaixa);
        talaoDAO.save(t);
        return t;
    }
}

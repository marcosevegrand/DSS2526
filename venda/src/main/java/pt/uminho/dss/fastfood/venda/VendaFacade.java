package pt.uminho.dss.fastfood.venda;

import pt.uminho.dss.fastfood.core.domain.Pedido;
import pt.uminho.dss.fastfood.core.domain.enums.ModoConsumo;
import pt.uminho.dss.fastfood.persistence.PedidoDAO;

public class VendaFacade implements IVenda {

    private final PedidoDAO pedidoDAO;

    public VendaFacade(PedidoDAO pedidoDAO) {
        this.pedidoDAO = pedidoDAO;
    }

    @Override
    public Pedido iniciarPedido(ModoConsumo modoConsumo, int idTerminal, int idFuncionario) {
        Pedido p = new Pedido(modoConsumo, idTerminal, idFuncionario);
        pedidoDAO.save(p);
        return p;
    }

    @Override
    public Pedido adicionarItem(int idPedido, int idProdutoOuMenu, String personalizacao, int quantidade) {
        Pedido p = pedidoDAO.findById(idPedido);
        // lógica de adicionar linha ao pedido...
        pedidoDAO.update(p);
        return p;
    }

    // Implementa os restantes métodos de IVenda aqui...
}

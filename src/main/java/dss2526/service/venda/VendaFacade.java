package dss2526.service.venda;

import dss2526.data.impl.PedidoDAOImpl;
import dss2526.data.contract.PedidoDAO;
import dss2526.domain.entity.Pedido;

public class VendaFacade implements IVendaFacade {

    private static VendaFacade instance;
    private final PedidoDAO pedidoDAO;

    private VendaFacade() {
        this.pedidoDAO = PedidoDAOImpl.getInstance();
    }

    public static synchronized VendaFacade getInstance() {
        if (instance == null) {
            instance = new VendaFacade();
        }
        return instance;
    }

    @Override
    public Pedido iniciarPedido(int restauranteId) {
        Pedido p = new Pedido();
        p.setRestauranteId(restauranteId);
        // Set initial state, timestamp, etc.
        return pedidoDAO.create(p);
    }

    @Override
    public void adicionarItem(int pedidoId, int itemId, int quantidade) {
        Pedido p = pedidoDAO.findById(pedidoId);
        // Logic to add LineItem
        // p.addLinha(...);
        pedidoDAO.update(p);
    }

    @Override
    public void fecharPedido(int pedidoId) {
        Pedido p = pedidoDAO.findById(pedidoId);
        // Change status to CONFIRMED
        pedidoDAO.update(p);
    }
}
package dss2526.data.contract;

import dss2526.domain.entity.Pedido;
import java.util.List;

public interface PedidoDAO extends GenericDAO<Pedido, Integer> {
    List<Pedido> findByRestaurante(int restauranteId);
}

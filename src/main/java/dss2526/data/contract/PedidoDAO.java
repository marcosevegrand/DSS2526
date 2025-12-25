package dss2526.data.contract;

import java.util.List;

import dss2526.domain.entity.Pedido;

public interface PedidoDAO extends GenericDAO<Pedido, Integer> {
    List<Pedido> getPendentes();
}

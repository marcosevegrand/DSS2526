package dss2526.data.contract;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import dss2526.domain.entity.Pedido;
import dss2526.domain.enumeration.EstadoPedido;

/**
 * DAO para Pedido — contrato mínimo usado pela fachada/ UI.
 */
public interface PedidoDAO extends GenericDAO<Pedido, Integer> {

    List<Pedido> findByEstado(EstadoPedido estado);

    List<Pedido> findByData(LocalDate data);

    Optional<Pedido> findUltimoPorTerminal(int idTerminal);
}

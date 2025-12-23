// ...existing code...
package pt.uminho.dss.restaurante.persistence.contract;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import pt.uminho.dss.restaurante.domain.entity.Pedido;
import pt.uminho.dss.restaurante.domain.enumeration.EstadoPedido;

/**
 * DAO para Pedido — contrato mínimo usado pela fachada/ UI.
 */
public interface PedidoDAO extends GenericDAO<Pedido, Integer> {

    List<Pedido> findByEstado(EstadoPedido estado);

    List<Pedido> findByData(LocalDate data);

    Optional<Pedido> findUltimoPorTerminal(int idTerminal);
}

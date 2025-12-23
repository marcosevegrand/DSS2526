package dss2526.data.contract;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import dss2526.domain.entity.Pedido;
import dss2526.domain.enumeration.EstadoPedido;

/**
 * DAO para Pedido — contrato mínimo usado pela fachada/ UI.
 */
public interface PedidoDAO {

    Optional<Pedido> findById(int id);

    List<Pedido> findAll();

    Pedido save(Pedido pedido);

    Pedido update(Pedido pedido);

    void delete(int id);

    List<Pedido> findByEstado(EstadoPedido estado);

    List<Pedido> findByData(LocalDate data);

    Optional<Pedido> findUltimoPorTerminal(int idTerminal);
}

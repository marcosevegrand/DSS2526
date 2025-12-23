package pt.uminho.dss.fastfood.persistence.contract;

import pt.uminho.dss.fastfood.core.domain.entity.Pedido;
import pt.uminho.dss.fastfood.core.domain.enumeration.EstadoPedido;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PedidoDAO extends GenericDAO<Pedido, Integer> {

    /**
     * Devolve todos os pedidos com um determinado estado
     * (por exemplo, PAGO, EM_ESPERA_PRODUCAO, etc.).
     */
    List<Pedido> findByEstado(EstadoPedido estado);

    /**
     * Devolve todos os pedidos criados num certo dia.
     * Útil para estatísticas e listagens.
     */
    List<Pedido> findByData(LocalDate data);

    /**
     * Procura o último pedido criado num terminal específico,
     * se precisares de saber qual foi o último número emitido.
     */
    Optional<Pedido> findUltimoPorTerminal(int idTerminal);
}

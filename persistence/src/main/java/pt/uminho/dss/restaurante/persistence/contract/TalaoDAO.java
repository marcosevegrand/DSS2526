package pt.uminho.dss.restaurante.persistence.contract;

import java.util.List;
import pt.uminho.dss.restaurante.core.domain.entity.Talao;

public interface TalaoDAO extends GenericDAO<Talao, Integer> {
    /**
     * Devolve o talão associado a um certo pedido, se existir.
     */
    Talao findByPedidoId(int idPedido);

    /**
     * Lista todos os talões emitidos num determinado dia,
     * se mais tarde quiseres estatísticas de talões.
     */
    List<Talao> findByData(java.time.LocalDate data);
}

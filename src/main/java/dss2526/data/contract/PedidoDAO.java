package dss2526.data.contract;

import dss2526.domain.entity.Pedido;
import java.time.LocalDate;
import java.util.List;

public interface PedidoDAO extends GenericDAO<Pedido, Integer> {
    
    List<Pedido> findByData(LocalDate data);
    
    List<Pedido> findAll();
}
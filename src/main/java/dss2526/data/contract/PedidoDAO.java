package dss2526.data.contract;

import dss2526.domain.entity.Pedido;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * Interface ajustada para usar tipos primitivos int
 */
public interface PedidoDAO extends GenericDAO<Pedido, Integer> {
    
    List<Pedido> findByData(LocalDate data);
    List<Pedido> findAll();
    
    // Métodos de compatibilidade com a VendaFacade
    default Pedido get(int id) {
        return findById(id);
    }
    
    default void put(int id, Pedido p) {
        // Se o id for 0, é um pedido novo (save)
        // Se o id for > 0, tentamos encontrar para decidir entre save ou update
        if (id > 0 && findById(id) != null) {
            update(p);
        } else {
            save(p);
        }
    }

    default Collection<Pedido> values() {
        return findAll();
    }
}
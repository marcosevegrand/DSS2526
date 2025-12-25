package dss2526.data.contract;

import dss2526.domain.entity.Restaurante;
import java.util.Collection;
import java.util.List;

public interface RestauranteDAO extends GenericDAO<Restaurante, Integer> {
    List<Restaurante> findAll();
    
    default Restaurante get(int id) {
        return findById(id);
    }
    
    default Collection<Restaurante> values() {
        return findAll();
    }
}
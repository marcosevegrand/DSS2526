package dss2526.data.contract;

import dss2526.domain.entity.Restaurante;
import java.util.List;

public interface RestauranteDAO extends GenericDAO<Restaurante, Integer> {
    List<Restaurante> findAllByCatalogo(int catalogoId);
    // Manages LinhaStock internally
}
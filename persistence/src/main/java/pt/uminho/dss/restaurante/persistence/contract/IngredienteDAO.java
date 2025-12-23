package pt.uminho.dss.restaurante.persistence.contract;

import java.util.List;
import java.util.Optional;

import pt.uminho.dss.restaurante.domain.entity.Ingrediente;

/**
 * DAO para Ingrediente — contrato mínimo usado pela aplicação.
 */
public interface IngredienteDAO extends GenericDAO<Ingrediente, Integer> {
}
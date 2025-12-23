package dss2526.data.contract;

import java.util.List;
import java.util.Optional;

import dss2526.domain.entity.Ingrediente;

/**
 * DAO para Ingrediente — contrato mínimo usado pela aplicação.
 */
public interface IngredienteDAO {

    Optional<Ingrediente> findById(int id);

    List<Ingrediente> findAll();

    Ingrediente save(Ingrediente ingrediente);

    Ingrediente update(Ingrediente ingrediente);

    void delete(int id);
}
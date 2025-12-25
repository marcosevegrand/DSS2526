package dss2526.data.contract;

import dss2526.domain.entity.Funcionario;
import java.util.List;

public interface FuncionarioDAO extends GenericDAO<Funcionario, Integer> {
    List<Funcionario> findByRestaurante(int restauranteId);
}
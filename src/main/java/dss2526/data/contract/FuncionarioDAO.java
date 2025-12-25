package dss2526.data.contract;

import dss2526.domain.entity.Funcionario;

public interface FuncionarioDAO extends GenericDAO<Funcionario, Integer> {
    Funcionario findByRestaurante(int restauranteId);
}
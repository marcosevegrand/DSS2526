package dss2526.data.contract;

import dss2526.domain.entity.Estacao;

public interface EstacaoDAO extends GenericDAO<Estacao, Integer> {
    Estacao findByRestaurante(int restauranteId);
}

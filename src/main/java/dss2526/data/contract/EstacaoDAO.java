package dss2526.data.contract;

import dss2526.domain.entity.Estacao;
import java.util.List;

public interface EstacaoDAO extends GenericDAO<Estacao, Integer> {
    List<Estacao> findAllByRestaurante(int restauranteId);
}
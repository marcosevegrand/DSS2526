package dss2526.data.contract;

import dss2526.domain.entity.Mensagem;
import java.util.List;

public interface MensagemDAO extends GenericDAO<Mensagem, Integer> {
    List<Mensagem> findAllByRestaurante(int restauranteId);
}
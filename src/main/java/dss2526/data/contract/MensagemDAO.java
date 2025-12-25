package dss2526.data.contract;

import dss2526.domain.entity.Mensagem;
import java.util.Collection;
import java.util.List;

public interface MensagemDAO extends GenericDAO<Mensagem, Integer> {

    // Método para listar todas as mensagens (útil para o histórico)
    List<Mensagem> findAll();

    // Métodos default para compatibilidade com a lógica de Map da Fachada
    default Mensagem get(int id) {
        return findById(id);
    }

    default Collection<Mensagem> values() {
        return findAll();
    }

    default void put(int id, Mensagem m) {
        if (id > 0 && findById(id) != null) {
            update(m);
        } else {
            save(m);
        }
    }
}
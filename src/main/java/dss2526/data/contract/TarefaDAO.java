package dss2526.data.contract;

import dss2526.domain.entity.Tarefa;
import dss2526.domain.enumeration.Trabalho;
import java.util.Collection;
import java.util.List;

public interface TarefaDAO extends GenericDAO<Tarefa, Integer> {

    // Listagem geral
    List<Tarefa> findAll();

    // Filtros específicos para a lógica de negócio (Cozinha/Operações)
    List<Tarefa> findByRestaurante(int restauranteId);
    List<Tarefa> findPendentesByRestaurante(int restauranteId);

    // Métodos de compatibilidade com a Fachada (Estilo Map)
    default Tarefa get(int id) {
        return findById(id);
    }

    default Collection<Tarefa> values() {
        return findAll();
    }

    default void put(int id, Tarefa t) {
        if (id > 0 && findById(id) != null) {
            update(t);
        } else {
            save(t);
        }
    }
}
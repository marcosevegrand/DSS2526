package dss2526.data.contract;

import dss2526.domain.entity.Tarefa;
import dss2526.domain.enumeration.EstacaoTrabalho;
import java.util.List;

public interface TarefaDAO extends GenericDAO<Tarefa, Integer> {
    List<Tarefa> findByEstacao(EstacaoTrabalho estacao);
}

package pt.uminho.dss.restaurante.persistence.contract;

import pt.uminho.dss.restaurante.domain.entity.Tarefa;
import pt.uminho.dss.restaurante.domain.enumeration.EstacaoTrabalho;
import java.util.List;

public interface TarefaDAO extends GenericDAO<Tarefa, Integer> {
    List<Tarefa> findByEstacao(EstacaoTrabalho estacao);
}

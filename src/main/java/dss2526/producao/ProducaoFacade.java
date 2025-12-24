package dss2526.producao;

import dss2526.domain.entity.Tarefa;
import dss2526.domain.enumeration.EstacaoTrabalho;

import java.util.List;
import java.util.stream.Collectors;

public class ProducaoFacade implements IProducaoFacade {

    private final List<Tarefa> tarefas;

    public ProducaoFacade(List<Tarefa> tarefas) {
        this.tarefas = tarefas;
    }

    @Override
    public List<Tarefa> listarTarefasPorEstacao(EstacaoTrabalho estacao) {
        return tarefas.stream()
                .filter(t -> t.getPasso().getEstacao() == estacao)
                .collect(Collectors.toList());
    }

    @Override
    public void concluirTarefa(int idTarefa) {
        Tarefa tarefa = tarefas.stream()
                .filter(t -> t.getId() == idTarefa)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada: ID " + idTarefa));

        tarefa.setConcluida(true);
    }
}
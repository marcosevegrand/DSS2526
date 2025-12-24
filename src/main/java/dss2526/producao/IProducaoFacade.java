package dss2526.producao;

import java.util.List;

import dss2526.domain.entity.Tarefa;
import dss2526.domain.enumeration.EstacaoTrabalho;

/**
 * Contrato mínimo para a camada de produção usado pela UI.
 */
public interface IProducaoFacade {

    /**
     * Lista todas as tarefas pendentes para uma estação de trabalho específica.
     */
    List<Tarefa> listarTarefasPorEstacao(EstacaoTrabalho estacao);

    /**
     * Marca uma tarefa como concluída.
     */
    void concluirTarefa(int idTarefa);
}
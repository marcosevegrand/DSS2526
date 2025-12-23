// ...existing code...
package pt.uminho.dss.restaurante.producao;

import java.util.List;

import pt.uminho.dss.restaurante.domain.entity.Tarefa;
import pt.uminho.dss.restaurante.domain.entity.Pedido;
import pt.uminho.dss.restaurante.domain.entity.Produto;
import pt.uminho.dss.restaurante.domain.enumeration.EstacaoTrabalho;

/**
 * Contrato mínimo para a camada de produção usado pela UI.
 */
public interface IProducao {
    List<Tarefa> listarTarefas(EstacaoTrabalho estacao);
    Tarefa criarTarefa(Pedido pedido, Produto produto, EstacaoTrabalho estacao);
    void marcarConcluida(Long id);
}
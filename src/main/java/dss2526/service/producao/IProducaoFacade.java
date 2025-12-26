package dss2526.service.producao;

import dss2526.domain.entity.Mensagem;
import dss2526.domain.entity.Tarefa;
import java.util.List;

public interface IProducaoFacade {

    List<Tarefa> consultarTarefasEstacao(int restauranteId, int estacaoId);

    List<Mensagem> consultarMensagens(int restauranteId);

    void concluirTarefa(int tarefaId);

    void registarAlertaStock(int restauranteId, int ingredienteId);

    void difundirMensagem(int restauranteId, String texto, boolean urgente);

    void atualizarStockLocal(int ingredienteId, int restauranteId, float quantidade);
}
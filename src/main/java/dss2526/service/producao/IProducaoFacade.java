package dss2526.service.producao;

import dss2526.domain.entity.Mensagem;
import dss2526.domain.entity.Tarefa;
import java.util.List;

public interface IProducaoFacade {
    List<Tarefa> consultarTarefasEstacao(int restauranteId, int estacaoId);
    void concluirTarefa(int tarefaId);
    List<Mensagem> consultarMensagens(int restauranteId);
    void difundirMensagem(int rId, String txt, boolean urg);
    void atualizarStockLocal(int iId, int rId, float qtd);
    void registarAlertaStock(int rId, int iId);
}
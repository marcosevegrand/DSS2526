package dss2526.service.producao;

import dss2526.domain.entity.Estacao;
import dss2526.domain.entity.Pedido;
import java.util.List;

public interface IProducaoFacade {
    List<Pedido> consultarFilaPedidos(int restauranteId);
    void atualizarEstadoTarefa(int tarefaId, boolean concluida);
    void atribuirTarefaEstacao(int tarefaId, int estacaoId);
    List<Estacao> getEstacoes(int restauranteId);
}
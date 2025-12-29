package dss2526.service.producao;

import dss2526.domain.entity.*;
import dss2526.service.base.IBaseFacade;
import java.util.List;
import java.util.Map;

public interface IProducaoFacade extends IBaseFacade {
    List<Tarefa> listarTarefasSincronizadas(int restauranteId, int estacaoId);
    void iniciarTarefa(int tarefaId);
    void concluirTarefa(int tarefaId);
    void atrasarTarefa(int tarefaId, int ingredienteIdFaltoso);

    List<Pedido> listarAguardaPagamento(int restauranteId);
    void processarPagamentoCaixa(int pedidoId);
    List<Pedido> listarProntos(int restauranteId);
    void confirmarEntrega(int pedidoId);
    void solicitarRefacaoItens(int pedidoId, List<Integer> linhasPedidoIds);
    
    Map<Pedido, Long> obterProgressoMonitor(int restauranteId);
}
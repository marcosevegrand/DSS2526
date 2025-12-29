package dss2526.service.producao;

import dss2526.domain.entity.*;
import dss2526.service.base.IBaseFacade;
import java.util.List;

/**
 * Facade Stateless para o subsistema de produção.
 */
public interface IProducaoFacade extends IBaseFacade {
    
    // Gestão de Processos de Cozinha
    void verificarNovosPedidos(int restauranteId);
    List<Tarefa> listarTarefasDisponiveisParaIniciar(int restauranteId, int estacaoId);
    List<Tarefa> listarTarefasEmExecucaoNaEstacao(int restauranteId, int estacaoId);
    
    /**
     * Inicia a execução de uma tarefa e consome os ingredientes do stock.
     */
    void iniciarTarefa(int tarefaId);
    void concluirTarefa(int tarefaId);
    void registarAtrasoTarefa(int tarefaId);

    // Gestão de Processos de Caixa
    List<Pedido> listarPedidosAguardandoPagamento(int restauranteId);
    void processarPagamento(int pedidoId);
    List<Pedido> listarPedidosProntosParaEntrega(int restauranteId);
    void confirmarEntrega(int pedidoId);
    void cancelarPedido(int pedidoId);
    void refazerLinhaPedido(int pedidoId, int linhaPedidoId);

    // Utilitários de Consulta
    List<Pedido> consultarMonitorGlobal(int restauranteId);
    List<Mensagem> consultarMensagensRestaurante(int restauranteId);
}
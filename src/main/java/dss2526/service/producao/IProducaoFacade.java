package dss2526.service.producao;

import dss2526.domain.entity.*;
import dss2526.service.base.IBaseFacade;
import java.time.Duration; // Import necessário
import java.util.List;
import java.util.Map;

public interface IProducaoFacade extends IBaseFacade {
    // --- Gestão de Tarefas ---
    List<Tarefa> listarTarefasParaIniciar(int restauranteId, int estacaoId);
    List<Tarefa> listarTarefasEmExecucao(int estacaoId);
    
    void iniciarTarefa(int tarefaId, int estacaoId); 
    void concluirTarefa(int tarefaId);
    
    // --- Gestão de Atrasos ---
    List<Ingrediente> listarIngredientesDaTarefa(int tarefaId);
    void atrasarTarefa(int tarefaId, int ingredienteIdFaltoso);

    // --- Gestão de Pedidos (Caixa) ---
    List<Pedido> listarAguardaPagamento(int restauranteId);
    
    // Alterado para retornar a estimativa de tempo
    Duration processarPagamentoCaixa(int pedidoId);
    
    List<Pedido> listarProntos(int restauranteId);
    void confirmarEntrega(int pedidoId);
    void solicitarRefacaoItens(int pedidoId, List<Integer> linhasPedidoIds);
    
    // --- Monitorização e Comunicação ---
    Map<Pedido, String> obterProgressoMonitor(int restauranteId);
    List<Mensagem> listarMensagens(int restauranteId);
}
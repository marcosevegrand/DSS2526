package dss2526.service.producao;

import java.util.List;

import dss2526.domain.entity.Ingrediente;
import dss2526.domain.entity.Mensagem;
import dss2526.domain.entity.Pedido;
import dss2526.domain.entity.Tarefa;
import dss2526.service.base.IBaseFacade;

public interface IProducaoFacade extends IBaseFacade {
    
    // --- Gestão de Tarefas da Estação ---
    /** * Retorna tarefas pendentes aplicando a lógica "Just-in-Time".
     * Apenas mostra tarefas se o tempo decorrido da tarefa mais longa do pedido permitir sincronização.
     */
    List<Tarefa> consultarTarefasOtimizadas(int restauranteId, int estacaoId);
    
    void iniciarTarefa(int tarefaId);
    
    /**
     * Conclui a tarefa. 
     * Se for tarefa de CAIXA, marca pedido como ENTREGUE.
     * Caso contrário, se for a última tarefa, marca como PRONTO.
     */
    void concluirTarefa(int tarefaId);
    
    void registarAtrasoPorFaltaIngrediente(int tarefaId, int ingredienteId);
    
    List<Ingrediente> listarIngredientesDaTarefa(int tarefaId);

    // --- Visão Global de Pedidos ---
    /** Lista pedidos ativos (exclui ENTREGUE). */
    List<Pedido> consultarPedidosEmProducao(int restauranteId);
    
    List<Tarefa> consultarTarefasDoPedido(int pedidoId);

    // --- Mensagens e Comunicação ---
    List<Mensagem> consultarMensagens(int restauranteId);
    
    void difundirMensagem(int restauranteId, String texto, boolean urgente);

    void reportarPedidoIncorreto(int pedidoId);

    void gerarTarefasCorrecao(int pedidoId);

    void verificarPedidosEsquecidos(int restauranteId);
}
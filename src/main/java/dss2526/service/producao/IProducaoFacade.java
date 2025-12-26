package dss2526.service.producao;

import java.util.List;

import dss2526.domain.entity.Pedido;
import dss2526.domain.entity.Tarefa;
import dss2526.domain.enumeration.EstadoPedido;
import dss2526.domain.enumeration.Trabalho;
import dss2526.service.base.IBaseFacade;

public interface IProducaoFacade extends IBaseFacade {
    
    // // --- Order Processing & Task Generation ---
    // void iniciarProducaoPedido(Integer pedidoId);
    // List<Tarefa> gerarTarefasParaPedido(Integer pedidoId);
    // void cancelarProducaoPedido(Integer pedidoId);
    
    // // --- Task Assignment & Execution ---
    // List<Tarefa> obterTarefasPendentesPorEstacao(Integer estacaoId);
    // List<Tarefa> obterTarefasPendentesPorTrabalho(Trabalho trabalho);
    // void iniciarTarefa(Integer tarefaId);
    // void concluirTarefa(Integer tarefaId);
    // void marcarTarefaAtrasada(Integer tarefaId);
    
    // // --- Order Status Management ---
    // void atualizarEstadoPedido(Integer pedidoId, EstadoPedido novoEstado);
    // EstadoPedido verificarEstadoPedido(Integer pedidoId);
    // boolean todosProdutosConcluidos(Integer pedidoId);
    
    // // --- Task Queue Management ---
    // List<Tarefa> listarTarefasPorEstado(EstadoTarefa estado);
    // List<Tarefa> listarTarefasEmAndamento();
    // List<Tarefa> listarTarefasPendentes();
    // List<Tarefa> listarTarefasAtrasadas();
    // int contarTarefasPendentesPorEstacao(Integer estacaoId);
    
    // // --- Production Monitoring ---
    // List<Pedido> listarPedidosEmProducao(Integer restauranteId);
    // List<Pedido> listarPedidosProntos(Integer restauranteId);
    // double calcularTempoMedioConclusao(Integer restauranteId);
    
    // // --- Stock Consumption ---
    // boolean verificarStockSuficiente(Integer restauranteId, Integer produtoId, int quantidade);
    // void consumirIngredientes(Integer restauranteId, Integer produtoId, int quantidade);
}
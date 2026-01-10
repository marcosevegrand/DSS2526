package dss2526.service.producao;

import dss2526.domain.entity.*;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Interface para o módulo de Produção.
 * Define apenas os métodos necessários para o módulo de produção (cozinha e caixa).
 *
 * NOTA: Não herda de IBaseFacade para respeitar o Interface Segregation Principle.
 */
public interface IProducaoFacade {

    // ============ LISTAGENS ============
    List<Restaurante> listarRestaurantes();
    List<Estacao> listarEstacoesDeRestaurante(int restauranteId);
    Estacao obterEstacao(int estacaoId);
    Passo obterPasso(int passoId);
    Pedido obterPedido(int pedidoId);
    Produto obterProduto(int produtoId);
    Menu obterMenu(int menuId);

    // ============ GESTÃO DE TAREFAS ============
    List<Tarefa> listarTarefasParaIniciar(int restauranteId, int estacaoId);
    List<Tarefa> listarTarefasEmExecucao(int estacaoId);
    void iniciarTarefa(int tarefaId, int estacaoId);
    void concluirTarefa(int tarefaId);

    // ============ GESTÃO DE ATRASOS ============
    List<Ingrediente> listarIngredientesDaTarefa(int tarefaId);
    void atrasarTarefa(int tarefaId, int ingredienteIdFaltoso);

    // ============ GESTÃO DE PEDIDOS (CAIXA) ============
    List<Pedido> listarAguardaPagamento(int restauranteId);
    Duration processarPagamentoCaixa(int pedidoId);
    List<Pedido> listarProntos(int restauranteId);
    void confirmarEntrega(int pedidoId);
    void solicitarRefacaoItens(int pedidoId, List<Integer> linhasPedidoIds);

    // ============ MONITORIZAÇÃO E COMUNICAÇÃO ============
    Map<Pedido, String> obterProgressoMonitor(int restauranteId);
    List<Mensagem> listarMensagens(int restauranteId);
}
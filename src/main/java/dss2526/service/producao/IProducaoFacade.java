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
    /**
     * Lista todos os restaurantes.
     * @return Lista de restaurantes
     */
    List<Restaurante> listarRestaurantes();
    
    /**
     * Lista todas as estações de um restaurante.
     * @param restauranteId ID do restaurante
     * @return Lista de estações
     */
    List<Estacao> listarEstacoesDeRestaurante(int restauranteId);
    
    /**
     * Obtém uma estação pelo seu ID.
     * @param estacaoId ID da estação
     * @return Estação ou null se não encontrada
     */
    Estacao obterEstacao(int estacaoId);
    
    /**
     * Obtém um passo pelo seu ID.
     * @param passoId ID do passo
     * @return Passo ou null se não encontrado
     */
    Passo obterPasso(int passoId);
    
    /**
     * Obtém um pedido pelo seu ID.
     * @param pedidoId ID do pedido
     * @return Pedido ou null se não encontrado
     */
    Pedido obterPedido(int pedidoId);
    
    /**
     * Obtém um produto pelo seu ID.
     * @param produtoId ID do produto
     * @return Produto ou null se não encontrado
     */
    Produto obterProduto(int produtoId);
    
    /**
     * Obtém um menu pelo seu ID.
     * @param menuId ID do menu
     * @return Menu ou null se não encontrado
     */
    Menu obterMenu(int menuId);
    
    // ============ GE STÃO DE TAREFAS ============
    /**
     * Lista tarefas pendentes (prontas para começar) numa estação de um restaurante.
     * Apenas retorna tarefas que a estação pode confecionar e que estão sincronizadas.
     * @param restauranteId ID do restaurante
     * @param estacaoId ID da estação
     * @return Lista de tarefas pendentes
     */
    List<Tarefa> listarTarefasParaIniciar(int restauranteId, int estacaoId);
    
    /**
     * Lista tarefas em execução ou atrasadas numa estação.
     * @param estacaoId ID da estação
     * @return Lista de tarefas em execução ou atrasadas
     */
    List<Tarefa> listarTarefasEmExecucao(int estacaoId);
    
    /**
     * Inicia a execução de uma tarefa numa estação.
     * @param tarefaId ID da tarefa
     * @param estacaoId ID da estação que vai executar
     */
    void iniciarTarefa(int tarefaId, int estacaoId);
    
    /**
     * Marca uma tarefa como concluída.
     * @param tarefaId ID da tarefa
     */
    void concluirTarefa(int tarefaId);
    
    // ============ GE STÃO DE ATRASOS ============
    /**
     * Lista todos os ingredientes necessários para uma tarefa.
     * @param tarefaId ID da tarefa
     * @return Lista de ingredientes
     */
    List<Ingrediente> listarIngredientesDaTarefa(int tarefaId);
    
    /**
     * Marca uma tarefa como atrasada devido a falta de ingrediente.
     * Cria uma mensagem de alerta.
     * @param tarefaId ID da tarefa
     * @param ingredienteIdFaltoso ID do ingrediente em falta
     */
    void atrasarTarefa(int tarefaId, int ingredienteIdFaltoso);

    // ============ GE STÃO DE PEDIDOS (CAIXA) ============
    /**
     * Lista pedidos aguardando pagamento.
     * @param restauranteId ID do restaurante
     * @return Lista de pedidos em estado AGUARDA_PAGAMENTO
     */
    List<Pedido> listarAguardaPagamento(int restauranteId);
    
    /**
     * Processa pagamento na caixa e retorna estimação de tempo de espera.
     * @param pedidoId ID do pedido
     * @return Duração estimada de preparação
     */
    Duration processarPagamentoCaixa(int pedidoId);
    
    /**
     * Lista pedidos prontos para entrega.
     * @param restauranteId ID do restaurante
     * @return Lista de pedidos em estado PRONTO
     */
    List<Pedido> listarProntos(int restauranteId);
    
    /**
     * Confirma a entrega de um pedido.
     * @param pedidoId ID do pedido
     */
    void confirmarEntrega(int pedidoId);
    
    /**
     * Solicita refacção de itens específicos de um pedido.
     * Cria novas tarefas e uma mensagem de alerta.
     * @param pedidoId ID do pedido
     * @param linhasPedidoIds IDs das linhas do pedido a refazer
     */
    void solicitarRefacaoItens(int pedidoId, List<Integer> linhasPedidoIds);
    
    // ============ MONITORIZAÇÃO E COMUNICAÇÃO ============
    /**
     * Obtém o progresso de todos os pedidos ativos de um restaurante.
     * @param restauranteId ID do restaurante
     * @return Mapa (Pedido -> String com progresso)
     */
    Map<Pedido, String> obterProgressoMonitor(int restauranteId);
    
    /**
     * Lista todas as mensagens de um restaurante.
     * @param restauranteId ID do restaurante
     * @return Lista de mensagens ordenadas por data decrescente
     */
    List<Mensagem> listarMensagens(int restauranteId);
}

package restaurante.business;

import restaurante.business.pedidos.Pedido;
import restaurante.business.estatisticas.Estatisticas;
import java.util.List;
import java.util.Map;

/**
 * Facade interface for the business layer
 * Provides all business operations for the restaurant system
 */
public interface IRestauranteFacade {
    
    // Authentication
    boolean autenticarFuncionario(String codigo, String palavraPasse);
    boolean autenticarGerente(String codigo, String palavraPasse);
    void logout();
    
    // Order Management (Client)
    Pedido iniciarPedido();
    void adicionarItemAoPedido(String pedidoId, String itemId, Map<String, Object> opcoes);
    void personalizarItem(String pedidoId, String itemId, List<String> ingredientesRemover, List<String> notas);
    double calcularTotalPedido(String pedidoId);
    boolean processarPagamento(String pedidoId, String metodoPagamento);
    String finalizarPedido(String pedidoId);
    
    // Order Queue (Employee)
    List<Pedido> consultarFilaDePedidos(String postoTrabalho);
    void iniciarPreparacaoPedido(String pedidoId, String funcionarioId);
    void concluirPreparacaoPedido(String pedidoId);
    void reportarAtrasoNoStock(String pedidoId, String ingrediente);
    
    // Order Pickup
    List<Pedido> consultarPedidosProntos();
    void marcarPedidoComoEntregue(String pedidoId);
    void reportarProblemaComPedido(String pedidoId, String descricaoProblema);
    
    // Statistics (Manager)
    Estatisticas consultarEstatisticasFaturacao(String dataInicio, String dataFim);
    Estatisticas consultarEstatisticasAtendimento(String dataInicio, String dataFim);
    Estatisticas consultarEstatisticasDesempenho(String dataInicio, String dataFim);
}

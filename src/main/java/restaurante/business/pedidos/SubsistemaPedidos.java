package restaurante.business.pedidos;

import restaurante.data.IRestauranteDAO;
import java.util.List;
import java.util.Map;

/**
 * Subsystem responsible for managing orders
 */
public class SubsistemaPedidos {
    
    private IRestauranteDAO dao;
    private int proximoPedidoId = 1;
    
    public SubsistemaPedidos(IRestauranteDAO dao) {
        this.dao = dao;
    }
    
    public Pedido criarNovoPedido() {
        Pedido pedido = new Pedido(proximoPedidoId++);
        // TODO: Save to DAO
        return pedido;
    }
    
    public void adicionarItem(String pedidoId, String itemId, Map<String, Object> opcoes) {
        // TODO: Implement
    }
    
    public void personalizarItem(String pedidoId, String itemId, List<String> ingredientesRemover, List<String> notas) {
        // TODO: Implement
    }
    
    public double calcularTotal(String pedidoId) {
        // TODO: Implement
        return 0.0;
    }
    
    public boolean processarPagamento(String pedidoId, String metodoPagamento) {
        // TODO: Implement
        return false;
    }
    
    public String finalizarPedido(String pedidoId) {
        // TODO: Implement
        return pedidoId;
    }
    
    public List<Pedido> obterFilaDePedidos(String postoTrabalho) {
        // TODO: Implement
        return null;
    }
    
    public void iniciarPreparacao(String pedidoId, String funcionarioId) {
        // TODO: Implement
    }
    
    public void concluirPreparacao(String pedidoId) {
        // TODO: Implement
    }
    
    public void reportarAtraso(String pedidoId, String ingrediente) {
        // TODO: Implement
    }
    
    public List<Pedido> obterPedidosProntos() {
        // TODO: Implement
        return null;
    }
    
    public void marcarComoEntregue(String pedidoId) {
        // TODO: Implement
    }
    
    public void reportarProblema(String pedidoId, String descricaoProblema) {
        // TODO: Implement
    }
}

package restaurante.business.terminais;

import restaurante.business.pedidos.Pedido;

/**
 * Represents a sales terminal for customers
 */
public class TerminalVenda {
    private int id;
    private Pedido pedidoAtual;
    
    public TerminalVenda(int id) {
        this.id = id;
        this.pedidoAtual = null;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Pedido getPedidoAtual() {
        return pedidoAtual;
    }
    
    public void iniciarPedido(Pedido pedido) {
        this.pedidoAtual = pedido;
    }
    
    public void adicionarItem(restaurante.business.pedidos.ItemPedido item) {
        if (pedidoAtual != null) {
            pedidoAtual.adicionarItem(item);
        }
    }
    
    public void removerItem(restaurante.business.pedidos.ItemPedido item) {
        if (pedidoAtual != null) {
            pedidoAtual.removerItem(item);
        }
    }
    
    public Pedido fecharPedido() {
        Pedido pedido = this.pedidoAtual;
        this.pedidoAtual = null;
        return pedido;
    }
    
    public void deferModoConsumo(String modo) {
        if (pedidoAtual != null) {
            pedidoAtual.setModoConsumo(modo);
        }
    }
}

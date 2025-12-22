package restaurante.business.terminais;

import restaurante.business.pedidos.Pedido;
import restaurante.business.pedidos.Produto;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a production terminal in the kitchen
 */
public class TerminalProducao {
    private int id;
    private List<Pedido> listaPedidosPendentes;
    
    public TerminalProducao(int id) {
        this.id = id;
        this.listaPedidosPendentes = new ArrayList<>();
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public List<Pedido> getListaPedidosPendentes() {
        return new ArrayList<>(listaPedidosPendentes);
    }
    
    public void adicionarPedido(Pedido pedido) {
        listaPedidosPendentes.add(pedido);
    }
    
    public void atualizarEstadoPedido(int idPedido, String novoEstado) {
        for (Pedido pedido : listaPedidosPendentes) {
            if (pedido.getId() == idPedido) {
                pedido.setEstadoPedido(novoEstado);
                if (novoEstado.equals("PRONTO")) {
                    listaPedidosPendentes.remove(pedido);
                }
                break;
            }
        }
    }
    
    public void notificarPronto(int idProduto, Produto produto) {
        // Notify that a product is ready
        System.out.println("Produto " + idProduto + " pronto!");
    }
}

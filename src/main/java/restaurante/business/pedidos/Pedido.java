package restaurante.business.pedidos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents an order in the system
 */
public class Pedido {
    private int id;
    private Date data;
    private String estadoPedido;
    private float preco;
    private String modoConsumo;
    private List<ItemPedido> items;
    
    public Pedido(int id) {
        this.id = id;
        this.data = new Date();
        this.estadoPedido = "EM_PREPARACAO";
        this.preco = 0.0f;
        this.modoConsumo = "LOCAL";
        this.items = new ArrayList<>();
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Date getData() {
        return data;
    }
    
    public void setData(Date data) {
        this.data = data;
    }
    
    public String getEstadoPedido() {
        return estadoPedido;
    }
    
    public void setEstadoPedido(String estadoPedido) {
        this.estadoPedido = estadoPedido;
    }
    
    public float getPreco() {
        return preco;
    }
    
    public void setPreco(float preco) {
        this.preco = preco;
    }
    
    public String getModoConsumo() {
        return modoConsumo;
    }
    
    public void setModoConsumo(String modoConsumo) {
        this.modoConsumo = modoConsumo;
    }
    
    public List<ItemPedido> getItems() {
        return new ArrayList<>(items);
    }
    
    public void adicionarItem(ItemPedido item) {
        this.items.add(item);
        recalcularPreco();
    }
    
    public void removerItem(ItemPedido item) {
        this.items.remove(item);
        recalcularPreco();
    }
    
    private void recalcularPreco() {
        this.preco = 0.0f;
        for (ItemPedido item : items) {
            this.preco += item.getPreco();
        }
    }
}

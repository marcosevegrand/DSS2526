package restaurante.business.pedidos;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents stock inventory for products
 */
public class Stock {
    private Map<Integer, ItemStock> itemsStock;
    
    public Stock() {
        this.itemsStock = new HashMap<>();
    }
    
    public void adicionarProduto(int idProduto, Produto produto, int quantidade) {
        if (itemsStock.containsKey(idProduto)) {
            itemsStock.get(idProduto).adicionarQuantidade(quantidade);
        } else {
            itemsStock.put(idProduto, new ItemStock(produto, quantidade));
        }
    }
    
    public ItemStock getItemStock(int idProduto) {
        return itemsStock.get(idProduto);
    }
    
    public boolean temStock(int idProduto, int quantidade) {
        ItemStock item = itemsStock.get(idProduto);
        return item != null && item.getQuantidade() >= quantidade;
    }
    
    public void consumirStock(int idProduto, int quantidade) {
        if (itemsStock.containsKey(idProduto)) {
            itemsStock.get(idProduto).removerQuantidade(quantidade);
        }
    }
    
    public Map<Integer, ItemStock> listarStock() {
        return new HashMap<>(itemsStock);
    }
}

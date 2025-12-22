package restaurante.business.pedidos;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Manages products and menus in the restaurant
 */
public class Catalogo {
    private Map<Integer, Produto> produtos;
    private Map<Integer, Menu> menus;
    
    public Catalogo() {
        this.produtos = new HashMap<>();
        this.menus = new HashMap<>();
    }
    
    public Produto getProduto(int idProduto) {
        return produtos.get(idProduto);
    }
    
    public void adicionarProduto(Produto produto) {
        produtos.put(produto.getId(), produto);
    }
    
    public void removerProduto(int idProduto) {
        produtos.remove(idProduto);
    }
    
    public Menu getMenu(int idMenu) {
        return menus.get(idMenu);
    }
    
    public void adicionarMenu(Menu menu) {
        menus.put(menu.getId(), menu);
    }
    
    public void removerMenu(int idMenu) {
        menus.remove(idMenu);
    }
    
    public List<Produto> listarProdutos() {
        return new ArrayList<>(produtos.values());
    }
    
    public List<Menu> listarMenus() {
        return new ArrayList<>(menus.values());
    }
}

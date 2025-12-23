package dss2526.ui.controller;

import dss2526.domain.entity.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

/**
 * Controlador responsável pela interação do Terminal de Vendas com o sistema.
 * Atualmente implementado como Mock para testes de UI.
 */
public class VendaController {

    // Simula a criação de um pedido
    public Pedido novoPedido() { 
        Pedido p = new Pedido();
        p.setId(123); // ID fictício
        return p; 
    }
    
    // Retorna uma lista estática de produtos para popular a grelha
    public List<Produto> listarProdutos() {
        List<Produto> lista = new ArrayList<>();
        
        Produto p1 = new Produto(); 
        p1.setId(1); p1.setNome("Big Burger"); p1.setPreco(BigDecimal.valueOf(5.50));
        
        Produto p2 = new Produto(); 
        p2.setId(2); p2.setNome("Cola Zero"); p2.setPreco(BigDecimal.valueOf(1.50));

        Produto p3 = new Produto(); 
        p3.setId(3); p3.setNome("Batata Frita"); p3.setPreco(BigDecimal.valueOf(2.00));
        
        lista.add(p1); lista.add(p2); lista.add(p3);
        return lista;
    }
    
    // Métodos stub para evitar erros de compilação na View
    public List<Menu> listarMenus() { return new ArrayList<>(); }
    public int getMenuIdByIndex(int i) { return 0; }
    public int getProdutoIdByIndex(int i) { return 0; }
    public void adicionarItem(int p, int i, int q, String n) {}
    public void removerItem(int p, int i, int q) {}
    public void adicionarNotaAoPedido(int id, String n) {}
    public void pagarPedido(int id) {}
    
    public Pedido obterPedido(int id) {
        Pedido p = new Pedido();
        p.setId(id);
        return p;
    }
}
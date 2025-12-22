package restaurante.business.pedidos;

import restaurante.data.IRestauranteDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Subsystem responsible for managing orders
 */
public class SubsistemaPedidos {
    
    private final IRestauranteDAO dao;
    private int proximoPedidoId = 1;
    private int proximoItemId = 1;
    
    public SubsistemaPedidos(IRestauranteDAO dao) {
        this.dao = dao;
        inicializarProximoId();
    }
    
    private void inicializarProximoId() {
        try {
            List<Pedido> pedidos = dao.obterTodosPedidos();
            for (Pedido p : pedidos) {
                proximoPedidoId = Math.max(proximoPedidoId, p.getId() + 1);
            }
        } catch (Exception e) {
            // BD vazia ou erro, usa ID 1
        }
    }
    
    public Pedido criarNovoPedido() {
        Pedido pedido = new Pedido(proximoPedidoId++);
        pedido.setEstadoPedido("ABERTO");  // Muda estado inicial para ABERTO
        dao.guardarPedido(pedido);
        return pedido;
    }
    
    public void adicionarItem(String pedidoIdStr, String produtoId, Map<String, Object> opcoes) {
        int pedidoId = Integer.parseInt(pedidoIdStr);
        Pedido pedido = dao.obterPedido(pedidoId);
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido não encontrado: " + pedidoIdStr);
        }
        
        // Busca produto/produto (ajusta método no DAO)
        Produto produto = dao.obterProduto(produtoId); // ou obterAlimento
        if (produto == null) {
            throw new IllegalArgumentException("Produto não encontrado: " + produtoId);
        }
        
        // Cria ItemPedido usando o teu construtor
        ItemPedido item = new ItemPedido(proximoItemId++, produto, 1);
        
        // Aplica personalização
        if (opcoes != null && !opcoes.isEmpty()) {
            String personalizacao = String.join(", ", opcoes.keySet());
            item.setPersonalizacao(personalizacao);
        }
        
        pedido.adicionarItem(item);  // Usa o teu método adicionarItem()
        dao.atualizarPedido(pedido);
    }
    
    public void personalizarItem(String pedidoIdStr, String itemIdStr, 
                                List<String> ingredientesRemover, List<String> notas) {
        int pedidoId = Integer.parseInt(pedidoIdStr);
        int itemId = Integer.parseInt(itemIdStr);
        Pedido pedido = dao.obterPedido(pedidoId);
        if (pedido == null) return;
        
        for (ItemPedido item : pedido.getItems()) {
            if (item.getId() == itemId) {
                item.setPersonalizacao(String.join(", ", ingredientesRemover));
                item.setNota(String.join("; ", notas));
                pedido.recalcularPreco();  // Usa o teu recalcularPreco() interno
                dao.atualizarPedido(pedido);
                return;
            }
        }
    }
    
    public double calcularTotal(String pedidoIdStr) {
        int pedidoId = Integer.parseInt(pedidoIdStr);
        Pedido pedido = dao.obterPedido(pedidoId);
        if (pedido == null) return 0.0;
        return pedido.getPreco();  // Usa getPreco() da tua classe
    }
    
    public boolean processarPagamento(String pedidoIdStr, String metodoPagamento) {
        int pedidoId = Integer.parseInt(pedidoIdStr);
        Pedido pedido = dao.obterPedido(pedidoId);
        if (pedido == null || pedido.getItems().isEmpty()) {
            return false;
        }
        
        if (!metodoPagamento.matches("(?i)(MBWAY|CARTAO|DINHEIRO)")) {
            return false;
        }
        
        pedido.setEstadoPedido("PAGO");
        pedido.setModoConsumo(metodoPagamento);  // Reusa modoConsumo para método
        dao.atualizarPedido(pedido);
        return true;
    }
    
    public String finalizarPedido(String pedidoIdStr) {
        int pedidoId = Integer.parseInt(pedidoIdStr);
        Pedido pedido = dao.obterPedido(pedidoId);
        if (pedido == null || !"PAGO".equals(pedido.getEstadoPedido())) {
            throw new IllegalStateException("Só pode finalizar pedidos pagos");
        }
        
        pedido.setEstadoPedido("EM_PREPARACAO");
        dao.atualizarPedido(pedido);
        return pedidoIdStr;
    }
    
    public List<Pedido> obterFilaDePedidos(String postoTrabalho) {
        List<Pedido> todos = dao.obterTodosPedidos();
        List<Pedido> fila = new ArrayList<>();
        
        for (Pedido p : todos) {
            if ("EM_PREPARACAO".equals(p.getEstadoPedido()) && 
                temItensParaPosto(p, postoTrabalho)) {
                fila.add(p);
            }
        }
        return fila;
    }
    
    public void iniciarPreparacao(String pedidoIdStr, String funcionarioId) {
        int pedidoId = Integer.parseInt(pedidoIdStr);
        Pedido pedido = dao.obterPedido(pedidoId);
        if (pedido == null) return;
        
        // Usa modoConsumo para guardar funcionário (ajusta conforme necessário)
        pedido.setModoConsumo("FUNC:" + funcionarioId);
        dao.atualizarPedido(pedido);
    }
    
    public void concluirPreparacao(String pedidoIdStr) {
        int pedidoId = Integer.parseInt(pedidoIdStr);
        Pedido pedido = dao.obterPedido(pedidoId);
        if (pedido == null) return;
        
        pedido.setEstadoPedido("PRONTO");
        dao.atualizarPedido(pedido);
    }
    
    public void reportarAtraso(String pedidoIdStr, String ingrediente) {
        int pedidoId = Integer.parseInt(pedidoIdStr);
        Pedido pedido = dao.obterPedido(pedidoId);
        if (pedido == null) return;
        
        // Usa personalizacao do primeiro item para notas (ajusta conforme necessário)
        if (!pedido.getItems().isEmpty()) {
            ItemPedido primeiroItem = pedido.getItems().get(0);
            primeiroItem.setNota(primeiroItem.getNota() + "; ATRASO: " + ingrediente);
        }
        dao.atualizarPedido(pedido);
    }
    
    public List<Pedido> obterPedidosProntos() {
        List<Pedido> todos = dao.obterTodosPedidos();
        return todos.stream()
                .filter(p -> "PRONTO".equals(p.getEstadoPedido()))
                .collect(Collectors.toList());
                }
    
    public void marcarComoEntregue(String pedidoIdStr) {
        int pedidoId = Integer.parseInt(pedidoIdStr);
        Pedido pedido = dao.obterPedido(pedidoId);
        if (pedido == null) return;
        
        pedido.setEstadoPedido("ENTREGUE");
        dao.atualizarPedido(pedido);
    }
    
    public void reportarProblema(String pedidoIdStr, String descricaoProblema) {
        int pedidoId = Integer.parseInt(pedidoIdStr);
        Pedido pedido = dao.obterPedido(pedidoId);
        if (pedido == null) return;
        
        pedido.setEstadoPedido("PROBLEMA");
        if (!pedido.getItems().isEmpty()) {
            pedido.getItems().get(0).setNota("PROBLEMA: " + descricaoProblema);
        }
        dao.atualizarPedido(pedido);
    }
    
private boolean temItensParaPosto(Pedido pedido, String postoTrabalho) {
    for (ItemPedido item : pedido.getItems()) {
        String tipoProduto = item.getProduto().getTipo(); 
        if (pertenceAoPosto(tipoProduto, postoTrabalho)) {
            return true;
        }
    }
    return false;
}
    
    private boolean pertenceAoPosto(String tipoProduto, String posto) {
        switch (posto) {
            case "GRELHA":
                return tipoProduto.toLowerCase().contains("hamburguer") || 
                       tipoProduto.toLowerCase().contains("carne");
            case "GELADOS":
                return tipoProduto.toLowerCase().contains("gelado");
            case "ENTREGA":
                return true;
            default:
                return false;
        }
    }
}

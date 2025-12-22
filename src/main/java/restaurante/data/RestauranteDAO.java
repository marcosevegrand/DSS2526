package restaurante.data;

import restaurante.business.pedidos.Pedido;
import restaurante.business.pedidos.Alimento;
import restaurante.business.funcionarios.Funcionario;
import restaurante.business.restaurantes.Restaurante;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory implementation of IRestauranteDAO
 * TODO: Replace with actual database implementation (MySQL, PostgreSQL, etc.)
 */
public class RestauranteDAO implements IRestauranteDAO {
    
    private Map<Integer, Pedido> pedidos;
    private Map<String, Alimento> alimentos;
    private Map<Integer, Funcionario> funcionarios;
    private Map<Integer, Restaurante> restaurantes;
    
    public RestauranteDAO() {
        this.pedidos = new HashMap<>();
        this.alimentos = new HashMap<>();
        this.funcionarios = new HashMap<>();
        this.restaurantes = new HashMap<>();
    }
    
    // Pedido operations
    @Override
    public void guardarPedido(Pedido pedido) {
        pedidos.put(pedido.getId(), pedido);
    }
    
    @Override
    public Pedido obterPedido(int id) {
        return pedidos.get(id);
    }
    
    @Override
    public List<Pedido> obterTodosPedidos() {
        return new ArrayList<>(pedidos.values());
    }
    
    @Override
    public void atualizarPedido(Pedido pedido) {
        pedidos.put(pedido.getId(), pedido);
    }
    
    @Override
    public void removerPedido(int id) {
        pedidos.remove(id);
    }
    
    // Alimento operations
    @Override
    public void guardarAlimento(Alimento alimento) {
        alimentos.put(alimento.getId(), alimento);
    }
    
    @Override
    public Alimento obterAlimento(String id) {
        return alimentos.get(id);
    }
    
    @Override
    public List<Alimento> obterTodosAlimentos() {
        return new ArrayList<>(alimentos.values());
    }
    
    @Override
    public void atualizarAlimento(Alimento alimento) {
        alimentos.put(alimento.getId(), alimento);
    }
    
    @Override
    public void removerAlimento(String id) {
        alimentos.remove(id);
    }
    
    // Funcionario operations
    @Override
    public void guardarFuncionario(Funcionario funcionario) {
        funcionarios.put(funcionario.getId(), funcionario);
    }
    
    @Override
    public Funcionario obterFuncionario(int id) {
        return funcionarios.get(id);
    }
    
    @Override
    public Funcionario obterFuncionarioPorUsername(String username) {
        return funcionarios.values().stream()
                .filter(f -> f.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public List<Funcionario> obterTodosFuncionarios() {
        return new ArrayList<>(funcionarios.values());
    }
    
    @Override
    public void atualizarFuncionario(Funcionario funcionario) {
        funcionarios.put(funcionario.getId(), funcionario);
    }
    
    @Override
    public void removerFuncionario(int id) {
        funcionarios.remove(id);
    }
    
    // Restaurante operations
    @Override
    public void guardarRestaurante(Restaurante restaurante) {
        restaurantes.put(restaurante.getId(), restaurante);
    }
    
    @Override
    public Restaurante obterRestaurante(int id) {
        return restaurantes.get(id);
    }
    
    @Override
    public List<Restaurante> obterTodosRestaurantes() {
        return new ArrayList<>(restaurantes.values());
    }
    
    @Override
    public void atualizarRestaurante(Restaurante restaurante) {
        restaurantes.put(restaurante.getId(), restaurante);
    }
    
    @Override
    public void removerRestaurante(int id) {
        restaurantes.remove(id);
    }
}

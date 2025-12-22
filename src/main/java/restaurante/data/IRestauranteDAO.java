package restaurante.data;

import restaurante.business.pedidos.Pedido;
import restaurante.business.pedidos.Alimento;
import restaurante.business.funcionarios.Funcionario;
import restaurante.business.restaurantes.Restaurante;
import java.util.List;

/**
 * Data Access Object interface for restaurant data persistence
 */
public interface IRestauranteDAO {
    
    // Pedido operations
    void guardarPedido(Pedido pedido);
    Pedido obterPedido(int id);
    List<Pedido> obterTodosPedidos();
    void atualizarPedido(Pedido pedido);
    void removerPedido(int id);
    
    // Alimento operations
    void guardarAlimento(Alimento alimento);
    Alimento obterAlimento(String id);
    List<Alimento> obterTodosAlimentos();
    void atualizarAlimento(Alimento alimento);
    void removerAlimento(String id);
    
    // Funcionario operations
    void guardarFuncionario(Funcionario funcionario);
    Funcionario obterFuncionario(int id);
    Funcionario obterFuncionarioPorUsername(String username);
    List<Funcionario> obterTodosFuncionarios();
    void atualizarFuncionario(Funcionario funcionario);
    void removerFuncionario(int id);
    
    // Restaurante operations
    void guardarRestaurante(Restaurante restaurante);
    Restaurante obterRestaurante(int id);
    List<Restaurante> obterTodosRestaurantes();
    void atualizarRestaurante(Restaurante restaurante);
    void removerRestaurante(int id);
}

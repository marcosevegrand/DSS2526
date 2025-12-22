package restaurante.business.restaurantes;

import restaurante.business.funcionarios.Funcionario;
import restaurante.business.pedidos.Catalogo;
import restaurante.business.pedidos.Pedido;
import restaurante.business.pedidos.Stock;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a restaurant in the chain
 */
public class Restaurante {
    private int id;
    private String nome;
    private String morada;
    private Catalogo catalogo;
    private Stock stock;
    private Map<Integer, Funcionario> funcionarios;
    private Map<Integer, restaurante.business.terminais.TerminalVenda> terminaisVenda;
    private Map<Integer, restaurante.business.terminais.TerminalProducao> terminaisProducao;
    private Map<Integer, Pedido> pedidos;
    
    public Restaurante(int id, String nome, String morada) {
        this.id = id;
        this.nome = nome;
        this.morada = morada;
        this.catalogo = new Catalogo();
        this.stock = new Stock();
        this.funcionarios = new HashMap<>();
        this.terminaisVenda = new HashMap<>();
        this.terminaisProducao = new HashMap<>();
        this.pedidos = new HashMap<>();
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getMorada() {
        return morada;
    }
    
    public void setMorada(String morada) {
        this.morada = morada;
    }
    
    public Catalogo getCatalogo() {
        return catalogo;
    }
    
    public Stock getStock() {
        return stock;
    }
    
    public Funcionario loginFuncionario(String username, String password) {
        for (Funcionario func : funcionarios.values()) {
            if (func.autenticar(username, password)) {
                return func;
            }
        }
        return null;
    }
    
    public restaurante.business.terminais.TerminalVenda getTerminalVenda(int idTerminal) {
        return terminaisVenda.get(idTerminal);
    }
    
    public restaurante.business.terminais.TerminalProducao getTerminalProducao(int idEstacao) {
        return terminaisProducao.get(idEstacao);
    }
    
    public void adicionarFuncionario(Funcionario funcionario) {
        funcionarios.put(funcionario.getId(), funcionario);
    }
    
    public void adicionarTerminalVenda(restaurante.business.terminais.TerminalVenda terminal) {
        terminaisVenda.put(terminal.getId(), terminal);
    }
    
    public void adicionarTerminalProducao(restaurante.business.terminais.TerminalProducao terminal) {
        terminaisProducao.put(terminal.getId(), terminal);
    }
    
    public void adicionarPedido(Pedido pedido) {
        pedidos.put(pedido.getId(), pedido);
    }
}

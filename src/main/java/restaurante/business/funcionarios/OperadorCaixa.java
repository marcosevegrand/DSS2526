package restaurante.business.funcionarios;

/**
 * Represents a cashier in the restaurant
 */
public class OperadorCaixa extends Funcionario {
    
    public OperadorCaixa(int id, String nome) {
        super(id, nome, "Operador de Caixa");
    }
    
    public String getTipo() {
        return "Operador de Caixa";
    }
}

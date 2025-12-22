package restaurante.business.funcionarios;

/**
 * Represents a manager in the restaurant
 */
public class Gerente extends Funcionario {
    
    public Gerente(int id, String nome) {
        super(id, nome, "Gerente");
    }
    

    public String getTipo() {
        return "Gerente";
    }
}

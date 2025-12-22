package restaurante.business.funcionarios;

/**
 * Represents a cook in the restaurant
 */
public class Cozinheiro extends Funcionario {
    
    public Cozinheiro(int id, String nome) {
        super(id, nome, "Cozinheiro");
    }
    
    public String getTipo() {
        return "Cozinheiro";
    }
}

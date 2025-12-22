package restaurante.business.funcionarios;

/**
 * Represents an employee in the restaurant
 */
public class Funcionario {
    private int id;
    private String nome;
    private String cargo;
    private String username;
    private String password;
    private EstacaoT estacao;
    
    public Funcionario(int id, String nome, String cargo) {
        this.id = id;
        this.nome = nome;
        this.cargo = cargo;
        this.username = "";
        this.password = "";
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
    
    public String getCargo() {
        return cargo;
    }
    
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public EstacaoT getEstacao() {
        return estacao;
    }
    
    public void setEstacao(EstacaoT estacao) {
        this.estacao = estacao;
    }
    
    public boolean autenticar(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }
}

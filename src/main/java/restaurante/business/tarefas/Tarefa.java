package restaurante.business.tarefas;

import restaurante.business.funcionarios.Funcionario;

/**
 * Represents a task assigned to an employee
 */
public class Tarefa {
    private String id;
    private String descricao;
    private String pedidoId;
    private Funcionario funcionarioAtribuido;
    private boolean concluida;
    
    public Tarefa(String id, String descricao, String pedidoId) {
        this.id = id;
        this.descricao = descricao;
        this.pedidoId = pedidoId;
        this.concluida = false;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getPedidoId() {
        return pedidoId;
    }
    
    public Funcionario getFuncionarioAtribuido() {
        return funcionarioAtribuido;
    }
    
    public void setFuncionarioAtribuido(Funcionario funcionarioAtribuido) {
        this.funcionarioAtribuido = funcionarioAtribuido;
    }
    
    public boolean isConcluida() {
        return concluida;
    }
    
    public void setConcluida(boolean concluida) {
        this.concluida = concluida;
    }
}

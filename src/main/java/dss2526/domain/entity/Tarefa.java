package dss2526.domain.entity;

public class Tarefa {
    int id;
    int idPasso;
    int idPedido;
    boolean concluido;

    // Construtores
    public Tarefa() {}

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdPasso() { return idPasso; }
    public void setIdPasso(int idPasso) { this.idPasso = idPasso; }

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public boolean isConcluido() { return concluido; }
    public void setConcluido(boolean concluido) { this.concluido = concluido; }

}

package dss2526.domain.entity;

public class LinhaEstacao {
    int id;
    int idTarefa;
    int idPedido;
    boolean concluido;

    // Construtores
    public LinhaEstacao() {}

    public LinhaEstacao(int idTarefa, int idPedido, boolean concluido) {
        this.idTarefa = idTarefa;
        this.idPedido = idPedido;
        this.concluido = concluido;
    }

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdTarefa() { return idTarefa; }
    public void setIdTarefa(int idTarefa) { this.idTarefa = idTarefa; }

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public boolean isConcluido() { return concluido; }
    public void setConcluido(boolean concluido) { this.concluido = concluido; }

}

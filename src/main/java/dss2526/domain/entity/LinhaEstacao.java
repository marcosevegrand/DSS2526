package dss2526.domain.entity;

public class LinhaEstacao {
    int id;
    Tarefa tarefa;
    boolean concluido;

    // Construtores
    public LinhaEstacao() {}

    public LinhaEstacao(Tarefa tarefa, boolean concluido) {
        this.tarefa = tarefa;
        this.concluido = concluido;
    }


    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Tarefa getTarefa() { return tarefa; }
    public void setTarefa(Tarefa tarefa) { this.tarefa = tarefa; }

    public boolean isConcluido() { return concluido; }
    public void setConcluido(boolean concluido) { this.concluido = concluido; }

}

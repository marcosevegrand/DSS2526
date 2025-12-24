package dss2526.domain.entity;

import java.io.Serializable;

public class PassoProducao implements Serializable {
    private int id;
    private String nome;
    private Estacao estacao;

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Estacao getEstacao() { return estacao; }
    public void setEstacao(Estacao estacao) { this.estacao = estacao; }
}

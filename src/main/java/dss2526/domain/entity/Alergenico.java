package pt.uminho.dss.restaurante.domain.entity;

public class Alergenico {
    public Integer id;
    public String nome;

    // Construtores

    public Alergenico() {}

    public Alergenico(String nome) {
        this.nome = nome;
    }

    // Getters e Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}

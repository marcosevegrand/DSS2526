package dss2526.domain.entity;

public class Ingrediente {
    private int id;
    private String nome;
    private String unidade;
    private String alergenico;

    // Construtores

    public Ingrediente() {}

    public Ingrediente(String nome, String unidade, String alergenico) {
        this.nome = nome;
        this.unidade = unidade;
        this.alergenico = alergenico;
    }

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }

    public String getAlergenico() { return alergenico; }
    public void setAlergenico(String alergenico) { this.alergenico = alergenico; }
}

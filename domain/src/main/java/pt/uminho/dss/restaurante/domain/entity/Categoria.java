package pt.uminho.dss.restaurante.domain.entity;

public class Categoria {
    public Long id;
    public String nome;
    public boolean ativa;

    public Categoria(String nome, boolean ativa) {
        this.nome = nome;
        this.ativa = ativa;
    }

    public Categoria() {}

    // Getters e Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }
}

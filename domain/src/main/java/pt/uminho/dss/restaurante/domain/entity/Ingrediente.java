// ...existing code...
package pt.uminho.dss.restaurante.domain.entity;

import java.util.Objects;

/**
 * Entidade Ingrediente — pública com construtor sem-args para uso pelos DAOs.
 */
public class Ingrediente {

    private Integer id;
    private String nome;

    // Construtor sem-args público
    public Ingrediente() {
    }

    public Ingrediente(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = Objects.requireNonNull(nome);
    }

    @Override
    public String toString() {
        return "Ingrediente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingrediente)) return false;
        Ingrediente that = (Ingrediente) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
// ...existing code...
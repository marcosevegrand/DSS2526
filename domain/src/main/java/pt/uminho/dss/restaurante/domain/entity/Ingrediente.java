package pt.uminho.dss.restaurante.domain.entity;

import java.io.Serializable;

/**
 * Entidade pura representando um ingrediente.
 */
public class Ingrediente implements Serializable {
    private Long id;
    private String nome;
    private String unidadeMedida;

    public Ingrediente() {}

    public Ingrediente(String nome, String unidadeMedida) {
        this.nome = nome;
        this.unidadeMedida = unidadeMedida;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }
}
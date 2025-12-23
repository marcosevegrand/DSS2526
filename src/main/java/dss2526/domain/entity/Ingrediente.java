package dss2526.domain.entity;

import java.io.Serializable;

import dss2526.domain.enumeration.Alergenico;

/**
 * Entidade pura representando um ingrediente.
 */
public class Ingrediente implements Serializable {
    private Integer id;
    private String nome;
    private String unidadeMedida;
    private Alergenico alergenico;

    // Construtores

    public Ingrediente() {}

    public Ingrediente(String nome, String unidadeMedida, Alergenico alergenico) {
        this.nome = nome;
        this.unidadeMedida = unidadeMedida;
        this.alergenico = alergenico;
    }

    // Getters e Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }

    public Alergenico getAlergenico() { return alergenico; }
    public void setAlergenico(Alergenico alergenico) { this.alergenico = alergenico; }
}

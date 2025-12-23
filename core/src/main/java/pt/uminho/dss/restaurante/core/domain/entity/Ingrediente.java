package pt.uminho.dss.restaurante.core.domain.entity;

import java.util.Set;
import pt.uminho.dss.restaurante.core.domain.enumeration.Alergenico;

public class Ingrediente {

    private int id;
    private String nome;
    private String unidadeMedida;
    private int tempoPreparacao;
    private Set<Alergenico> alergenicos;

    public Ingrediente(
        int id,
        String nome,
        String unidadeMedida,
        Set<Alergenico> alergenicos
    ) {
        this.id = id;
        this.nome = nome;
        this.unidadeMedida = unidadeMedida;
        this.alergenicos = alergenicos;
    }

    // Construtor vazio para ORM / frameworks
    protected Ingrediente() {}

    // GETTERS & SETTERS

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public int getTempoPreparacao() {
        return tempoPreparacao;
    }

    public void setTempoPreparacao(int tempoPreparacao) {
        this.tempoPreparacao = tempoPreparacao;
    }

    public Set<Alergenico> getAlergenicos() {
        return alergenicos;
    }

    public void setAlergenicos(Set<Alergenico> alergenicos) {
        this.alergenicos = alergenicos;
    }

    public void addAlergenico(Alergenico alergenico) {
        this.alergenicos.add(alergenico);
    }

    public void removeAlergenico(Alergenico alergenico) {
        this.alergenicos.remove(alergenico);
    }
}

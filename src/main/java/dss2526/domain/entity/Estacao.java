package dss2526.domain.entity;

import dss2526.domain.enumeration.Trabalho;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe base abstrata para as Estações de Trabalho.
 * Implementa polimorfismo através das subclasses Cozinha e Caixa.
 */
public abstract class Estacao {
    private int id;
    private String nome;
    private int restauranteId;

    public Estacao() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getRestauranteId() { return restauranteId; }
    public void setRestauranteId(int restauranteId) { this.restauranteId = restauranteId; }

    // Capacidades baseadas no tipo de objeto (Polimorfismo)
    public abstract boolean podeProcessarPagamentos();
    public abstract boolean podeEntregarPedidos();
    public abstract boolean podeConfecionar(Trabalho t);

    // --- Subclasses Estáticas ---

    public static class Cozinha extends Estacao {
        private Set<Trabalho> especialidades = new HashSet<>();

        public void setEspecialidades(Set<Trabalho> especialidades) { this.especialidades = especialidades; }
        public Set<Trabalho> getEspecialidades() { return especialidades; }
        public void addEspecialidade(Trabalho t) { this.especialidades.add(t); }
        public void removeEspecialidade(Trabalho t) { this.especialidades.remove(t); }

        @Override public boolean podeProcessarPagamentos() { return false; }
        @Override public boolean podeEntregarPedidos() { return false; }
        @Override public boolean podeConfecionar(Trabalho t) { return especialidades.contains(t); }
    }

    public static class Caixa extends Estacao {
        @Override public boolean podeProcessarPagamentos() { return true; }
        @Override public boolean podeEntregarPedidos() { return true; }
        @Override public boolean podeConfecionar(Trabalho t) { return false; }
    }
}
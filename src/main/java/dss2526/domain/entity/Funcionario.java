package dss2526.domain.entity;

import dss2526.domain.enumeration.Funcao;

public class Funcionario {
    private int id;
    private int restauranteId;
    private String nome;
    private String utilizador;
    private String password;
    private Funcao papel;

    public Funcionario() {}

    public Funcionario(String nome, String utilizador, String password, Funcao papel, int restauranteId) {
        this.nome = nome;
        this.utilizador = utilizador;
        this.password = password;
        this.papel = papel;
        this.restauranteId = restauranteId;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getUtilizador() { return utilizador; }
    public void setUtilizador(String utilizador) { this.utilizador = utilizador; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Funcao getPapel() { return papel; }
    public void setPapel(Funcao papel) { this.papel = papel; }

    public Integer getRestauranteId() { return restauranteId; }
    public void setRestauranteId(Integer restauranteId) { this.restauranteId = restauranteId; }

    // Métodos utilitários de permissão
    public boolean podeAcederGestaoGlobal() {
        return this.papel == Funcao.COO;
    }


    public boolean podeVerEstatisticas(Integer idAlvo) {
        if (this.papel == Funcao.COO) {
            return true; 
        }
        if (this.papel == Funcao.CHEFERESTAURANTE && idAlvo != null) {
            return this.restauranteId == idAlvo;
        }
        return false;
    }
}
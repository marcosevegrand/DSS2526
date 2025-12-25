package dss2526.domain.entity;

import dss2526.domain.enumeration.RoleTrabalhador;
import java.io.Serializable;

public class Funcionario implements Serializable {
    private int id;
    private String nome;
    private String username;
    private String password;
    private RoleTrabalhador papel;
    private int restauranteId; // ID do restaurante onde trabalha (0 ou null se for COO)

    public Funcionario() {}

    public Funcionario(String nome, String username, String password, RoleTrabalhador papel, int restauranteId) {
        this.nome = nome;
        this.username = username;
        this.password = password;
        this.papel = papel;
        this.restauranteId = restauranteId;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public RoleTrabalhador getPapel() { return papel; }
    public void setPapel(RoleTrabalhador papel) { this.papel = papel; }

    public Integer getRestauranteId() { return restauranteId; }
    public void setRestauranteId(Integer restauranteId) { this.restauranteId = restauranteId; }

    // Métodos utilitários de permissão
    public boolean podeAcederGestaoGlobal() {
        return this.papel == RoleTrabalhador.COO;
    }


    public boolean podeVerEstatisticas(Integer idAlvo) {
        if (this.papel == RoleTrabalhador.COO) {
            return true; 
        }
        if (this.papel == RoleTrabalhador.CHEFERESTAURANTE && idAlvo != null) {
            return this.restauranteId == idAlvo;
        }
        return false;
    }
}
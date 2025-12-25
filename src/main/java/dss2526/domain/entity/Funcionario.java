package dss2526.domain.entity;

import dss2526.domain.enumeration.RoleTrabalhador;
import java.io.Serializable;

public class Funcionario implements Serializable {
    private Integer id;
    private String nome;
    private String username;
    private String password;
    private RoleTrabalhador papel;
    private Integer restauranteId; // ID do restaurante onde trabalha (0 ou null se for COO)

    public Funcionario() {}

    public Funcionario(String nome, String username, String password, RoleTrabalhador papel, Integer restauranteId) {
        this.nome = nome;
        this.username = username;
        this.password = password;
        this.papel = papel;
        this.restauranteId = restauranteId;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

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
        if (this.papel == RoleTrabalhador.COO) return true;
        return this.papel == RoleTrabalhador.CHEFERESTAURANTE && this.restauranteId.equals(idAlvo);
    }
}
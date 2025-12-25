package dss2526.domain.entity;

import dss2526.domain.enumeration.Funcao;

public class Funcionario {
    private int id;
    private int restauranteId;
    private String utilizador;
    private String password;
    private Funcao funcao;

    public Funcionario() {}

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getRestauranteId() { return restauranteId; }
    public void setRestauranteId(Integer restauranteId) { this.restauranteId = restauranteId; }

    public String getUtilizador() { return utilizador; }
    public void setUtilizador(String utilizador) { this.utilizador = utilizador; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Funcao getFuncao() { return funcao; }
    public void setFuncao(Funcao funcao) { this.funcao = funcao; }

    @Override
    public String toString() {
        return "Funcionario{" +
                "id=" + id +
                ", restauranteId=" + restauranteId +
                ", utilizador='" + utilizador + '\'' +
                ", funcao=" + funcao +
                '}';
    }
}
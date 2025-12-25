package dss2526.domain.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Restaurante implements Serializable {
    private int id;
    private String nome;
    private String localizacao;

    private List<Pedido> pedidos;
    private List<Funcionario> funcionarios;
    private List<Estacao> estacoes;
    private List<Stock> inventario;

    public Restaurante() {
        this.pedidos = new ArrayList<>();
        this.funcionarios = new ArrayList<>();
        this.estacoes = new ArrayList<>();
        this.inventario = new ArrayList<>();
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public List<Pedido> getPedidos() { return pedidos; }
    public List<Funcionario> getFuncionarios() { return funcionarios; }
    public List<Estacao> getEstacoes() { return estacoes; }
    public List<Stock> getInventario() { return inventario; }

    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
}
package dss2526.domain.entity;

import java.util.ArrayList;
import java.util.List;

public class Restaurante {
    private int id;
    private String nome;
    private String localizacao;

    private List<Estacao> estacoes = new ArrayList<>();
    private List<Funcionario> funcionarios = new ArrayList<>();
    private List<LinhaStock> stock = new ArrayList<>();
    private List<Pedido> pedidos = new ArrayList<>();
    
    // Construtores

    public Restaurante() {}

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }

    public List<Estacao> getEstacoes() { return estacoes; }
    public void setEstacoes(List<Estacao> estacoes) { this.estacoes = estacoes; }
    public void adicionaEstacao(Estacao estacao) { this.estacoes.add(estacao); }
    public void removeEstacao(Estacao estacao) { this.estacoes.remove(estacao); }

    public List<Funcionario> getFuncionarios() { return funcionarios; }
    public void setFuncionarios(List<Funcionario> funcionarios) { this.funcionarios = funcionarios; }
    public void adicionaFuncionario(Funcionario funcionario) { this.funcionarios.add(funcionario); }
    public void removeFuncionario(Funcionario funcionario) { this.funcionarios.remove(funcionario); }

    public List<LinhaStock> getStock() { return stock; }
    public void setStock(List<LinhaStock> stock) { this.stock = stock; }
    public void adicionaLinhaStock(LinhaStock linhaStock) { this.stock.add(linhaStock); }
    public void removeLinhaStock(LinhaStock linhaStock) { this.stock.remove(linhaStock); }

    public List<Pedido> getPedidos() { return pedidos; }
    public void setPedidos(List<Pedido> pedidos) { this.pedidos = pedidos; }
    public void adicionaPedido(Pedido pedido) { this.pedidos.add(pedido); }
    public void removePedido(Pedido pedido) { this.pedidos.remove(pedido); }
}
package dss2526.domain.entity;

import java.util.*;

public class Produto {
    private int id;
    private String nome;
    private double preco;
    private List<Integer> passoIds = new ArrayList<>();
    private List<LinhaProduto> linhas = new ArrayList<>();

    // Construtores

    public Produto() {}

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public List<Integer> getPassoIds() { return passoIds; }
    public void setPassoIds(List<Integer> passoIds) { this.passoIds = passoIds; }
    public void addPassoId(Integer passoId) { this.passoIds.add(passoId); }
    public void removePassoId(Integer passoId) { this.passoIds.remove(passoId); }

    public List<LinhaProduto> getLinhas() { return linhas; }
    public void setLinhas(List<LinhaProduto> linhas) { this.linhas = linhas; }
    public void addLinha(LinhaProduto linha) { this.linhas.add(linha); }
    public void removeLinha(LinhaProduto linha) { this.linhas.remove(linha); }

    @Override
    public String toString() {
        return "Produto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", preco=" + preco +
                ", passoIds=" + passoIds +
                ", linhas=" + linhas +
                '}';
    }
}

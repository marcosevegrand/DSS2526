// ...existing code...
package pt.uminho.dss.restaurante.domain.entity;

import pt.uminho.dss.restaurante.domain.contract.Item;
import pt.uminho.dss.restaurante.domain.enumeration.Alergenico;
import pt.uminho.dss.restaurante.domain.enumeration.EstacaoTrabalho;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Produto como POJO puro. As coleções podem ser Lazy-Loaded pelo ORM
 * se configuradas via XML no módulo de persistência.
 */
public class Produto implements Item, Serializable {
    private Long id;
    private String nome;
    private BigDecimal preco;
    private String descricao;
    private EstacaoTrabalho estacaoTrabalho;
    private Set<Alergenico> alergenicos = new HashSet<>();
    private List<LinhaIngrediente> ingredientes = new ArrayList<>();

    public Produto() {}

    @Override public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    @Override public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    @Override public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    @Override public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public EstacaoTrabalho getEstacaoTrabalho() { return estacaoTrabalho; }
    public void setEstacaoTrabalho(EstacaoTrabalho estacaoTrabalho) { this.estacaoTrabalho = estacaoTrabalho; }
    public Set<Alergenico> getAlergenicos() { return alergenicos; }
    public void setAlergenicos(Set<Alergenico> alergenicos) { this.alergenicos = alergenicos; }
    public List<LinhaIngrediente> getIngredientes() { return ingredientes; }
    public void setIngredientes(List<LinhaIngrediente> ingredientes) { this.ingredientes = ingredientes; }
}

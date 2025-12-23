package pt.uminho.dss.restaurante.domain.entity;

import pt.uminho.dss.restaurante.domain.contract.Item;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Menu implements Item, Serializable {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private List<LinhaMenu> linhasMenu = new ArrayList<>();

    public Menu() {}

    @Override public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    @Override public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    @Override public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    @Override public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public List<LinhaMenu> getLinhasMenu() { return linhasMenu; }
    public void setLinhasMenu(List<LinhaMenu> linhasMenu) { this.linhasMenu = linhasMenu; }
}
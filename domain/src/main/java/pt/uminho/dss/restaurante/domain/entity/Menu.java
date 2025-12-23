package pt.uminho.dss.restaurante.domain.entity;

import pt.uminho.dss.restaurante.domain.contract.Item;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Menu implements Item, Serializable {
    private Integer id;
    private String nome;
    private BigDecimal preco;
    private boolean disponivel;
    private List<LinhaMenu> linhasMenu = new ArrayList<>();

    // Construtores

    public Menu() {}

    public Menu(String nome, BigDecimal preco, List<LinhaMenu> linhasMenu) {
        this.nome = nome;
        this.preco = preco;
        this.linhasMenu = linhasMenu;
    }

    // Getters e Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }

    public List<LinhaMenu> getLinhasMenu() { return linhasMenu; }
    public void setLinhasMenu(List<LinhaMenu> linhasMenu) { this.linhasMenu = linhasMenu; }
}
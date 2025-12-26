package dss2526.domain.entity;

import java.util.ArrayList;
import java.util.List;

public class Restaurante {
    private int id;
    private String nome;
    private String localizacao;
    private Integer catalogoId;

    // Referências por ID (Entidades independentes)
    private List<Integer> estacaoIds = new ArrayList<>();
    private List<Integer> funcionarioIds = new ArrayList<>();
    private List<Integer> pedidoIds = new ArrayList<>();

    // Composição direta (Parte do estado do Restaurante)
    private List<LinhaStock> stock = new ArrayList<>();
    
    // Construtores

    public Restaurante() {}

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }

    public Integer getCatalogoId() { return catalogoId; }
    public void setCatalogoId(Integer catalogoId) { this.catalogoId = catalogoId; }

    // Gestão de IDs de Estações
    public List<Integer> getEstacaoIds() { return estacaoIds; }
    public void setEstacaoIds(List<Integer> estacaoIds) { this.estacaoIds = estacaoIds; }
    public void addEstacaoId(Integer estacaoId) { this.estacaoIds.add(estacaoId); }
    public void removeEstacaoId(Integer estacaoId) { this.estacaoIds.remove(estacaoId); }

    // Gestão de IDs de Funcionários
    public List<Integer> getFuncionarioIds() { return funcionarioIds; }
    public void setFuncionarioIds(List<Integer> funcionarioIds) { this.funcionarioIds = funcionarioIds; }
    public void addFuncionarioId(Integer funcionarioId) { this.funcionarioIds.add(funcionarioId); }
    public void removeFuncionarioId(Integer funcionarioId) { this.funcionarioIds.remove(funcionarioId); }

    // Gestão de IDs de Pedidos
    public List<Integer> getPedidoIds() { return pedidoIds; }
    public void setPedidoIds(List<Integer> pedidoIds) { this.pedidoIds = pedidoIds; }
    public void addPedidoId(Integer pedidoId) { this.pedidoIds.add(pedidoId); }
    public void removePedidoId(Integer pedidoId) { this.pedidoIds.remove(pedidoId); }

    // Gestão de Stock (Instâncias reais)
    public List<LinhaStock> getStock() { return stock; }
    public void setStock(List<LinhaStock> stock) { this.stock = stock; }
    public void addLinhaStock(LinhaStock linhaStock) { this.stock.add(linhaStock); }
    public void removeLinhaStock(LinhaStock linhaStock) { this.stock.remove(linhaStock); }
}
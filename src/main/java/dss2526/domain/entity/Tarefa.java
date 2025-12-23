package dss2526.domain.entity;

import dss2526.domain.enumeration.EstacaoTrabalho;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entidade Tarefa desacoplada da persistência.
 * O Lazy Loading de 'pedido' ou 'produto' será gerido externamente pelo ORM.
 */
public class Tarefa implements Serializable {
    private Integer id;
    private Pedido pedido;
    private Produto produto;
    private PassoProducao passo;
    private Boolean concluida;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataConclusao;

    // Construtor

    public Tarefa() {
        this.concluida = false;
        this.dataCriacao = LocalDateTime.now();
    }

    // Lógica Simples

    public EstacaoTrabalho getEstacao() { return passo.getEstacao(); }

    // Getters e Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    
    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }
    
    public void setPasso(PassoProducao passo) { this.passo = passo; }
    public PassoProducao getPasso() { return passo; }

    public Boolean getConcluida() { return concluida; }

    public void setConcluida(Boolean concluida) { 
        this.concluida = concluida;
        if (concluida) this.dataConclusao = LocalDateTime.now();
    }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataConclusao() { return dataConclusao; }
}
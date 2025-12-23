package pt.uminho.dss.restaurante.domain.entity;

import pt.uminho.dss.restaurante.domain.enumeration.EstacaoTrabalho;
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
    private EstacaoTrabalho estacao;
    private Boolean concluida;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataConclusao;

    // Construtor

    public Tarefa() {
        this.concluida = false;
        this.dataCriacao = LocalDateTime.now();
    }

    // Lógica Simples

    // Getters e Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public void setPasso(PassoProducao passo) {
        this.passo = passo;
    }

    public PassoProducao getPasso() {
        return passo;
    }

    public EstacaoTrabalho getEstacao() {
        if (estacao != null)
            return estacao;
        return passo != null ? passo.getEstacao() : null;
    }

    public void setEstacao(EstacaoTrabalho estacao) {
        this.estacao = estacao;
    }

    public Boolean getConcluida() {
        return concluida;
    }

    public void setConcluida(Boolean concluida) {
        this.concluida = concluida;
        if (concluida)
            this.dataConclusao = LocalDateTime.now();
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(LocalDateTime dataConclusao) {
        this.dataConclusao = dataConclusao;
    }
}
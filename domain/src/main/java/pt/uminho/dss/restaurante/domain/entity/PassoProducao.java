package pt.uminho.dss.restaurante.domain.entity;

import pt.uminho.dss.restaurante.domain.enumeration.EstacaoTrabalho;

import java.io.Serializable;

public class PassoProducao implements Serializable {
    private Long id;
    private String nome;
    private EstacaoTrabalho estacao;


    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public EstacaoTrabalho getEstacao() { return estacao; }
    public void setEstacao(EstacaoTrabalho estacao) { this.estacao = estacao; }

}

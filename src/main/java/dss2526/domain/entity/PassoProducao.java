package dss2526.domain.entity;

import dss2526.domain.enumeration.EstacaoTrabalho;

import java.io.Serializable;

public class PassoProducao implements Serializable {
    private Integer id;
    private String nome;
    private EstacaoTrabalho estacao;


    
    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public EstacaoTrabalho getEstacao() { return estacao; }
    public void setEstacao(EstacaoTrabalho estacao) { this.estacao = estacao; }

}

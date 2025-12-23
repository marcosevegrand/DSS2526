package pt.uminho.dss.restaurante.ui.controller;

import java.util.List;
import java.util.Objects;

import pt.uminho.dss.restaurante.producao.IProducao;
import pt.uminho.dss.restaurante.domain.entity.Tarefa;
import pt.uminho.dss.restaurante.domain.enumeration.EstacaoTrabalho;

/**
 * Controller usado pela UI do terminal de produção.
 */
public class ProducaoController {

    private final IProducao producao;

    public ProducaoController(IProducao producao) {
        this.producao = Objects.requireNonNull(producao);
    }

    public List<Tarefa> listarTarefasPorEstacao(EstacaoTrabalho estacao) {
        return producao.listarTarefas(estacao);
    }

    public void concluirTarefa(Integer id) {
        producao.marcarConcluida(id);
    }
}
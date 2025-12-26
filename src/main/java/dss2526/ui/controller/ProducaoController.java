package dss2526.ui.controller;

import dss2526.domain.entity.*;
import dss2526.service.producao.IProducaoFacade;
import dss2526.data.contract.RestauranteDAO;
import dss2526.data.contract.EstacaoDAO;

import java.util.List;
import java.util.stream.Collectors;

public class ProducaoController {
    private final IProducaoFacade facade;
    private final RestauranteDAO restauranteDAO; // Adicionado para suporte à UI
    private final EstacaoDAO estacaoDAO;         // Adicionado para suporte à UI
    
    private int restauranteIdAtivo = -1;
    private int estacaoIdAtiva = -1;

    public ProducaoController(IProducaoFacade facade, RestauranteDAO rDAO, EstacaoDAO eDAO) {
        this.facade = facade;
        this.restauranteDAO = rDAO;
        this.estacaoDAO = eDAO;
    }

    // --- Métodos de Suporte à UI (Listagens iniciais) ---

    /**
     * Retorna apenas os nomes dos restaurantes para o método escolher() da UI.
     */
    public List<String> listarNomesRestaurantes() {
        return restauranteDAO.findAll().stream()
                .map(r -> r.getNome() + " (" + r.getLocalizacao() + ")")
                .collect(Collectors.toList());
    }

    /**
     * Retorna os nomes das estações de um restaurante específico.
     * @param restauranteIndex o índice selecionado na UI (0, 1, 2...)
     */
    public List<String> listarNomesEstacoes(int restauranteIndex) {
        Restaurante r = restauranteDAO.findAll().get(restauranteIndex);
        return estacaoDAO.findAll().stream()
                .filter(e -> e.getRestauranteId() == r.getId())
                .map(e -> "Estação #" + e.getId() + " - " + e.getTrabalho())
                .collect(Collectors.toList());
    }

    /**
     * Converte os índices da UI em IDs reais da Base de Dados e fixa o contexto.
     */
    public void selecionarContexto(int restIdx, int estIdx) {
        Restaurante r = restauranteDAO.findAll().get(restIdx);
        List<Estacao> estacoesDoRest = estacaoDAO.findAll().stream()
                .filter(e -> e.getRestauranteId() == r.getId())
                .collect(Collectors.toList());
        
        this.restauranteIdAtivo = r.getId();
        this.estacaoIdAtiva = estacoesDoRest.get(estIdx).getId();
    }

    // --- Métodos de Produção (Já tinhas) ---

    public List<Tarefa> getFilaTrabalho() {
        validarSessao();
        return facade.consultarTarefasEstacao(restauranteIdAtivo, estacaoIdAtiva);
    }

    public List<Mensagem> getMensagensGestao() {
        validarSessao();
        return facade.consultarMensagens(restauranteIdAtivo);
    }

    public void concluirTarefa(int tarefaId) {
        validarSessao();
        facade.concluirTarefa(tarefaId);
    }

    public void solicitarIngrediente(int ingredienteId) {
        validarSessao();
        facade.registarAlertaStock(restauranteIdAtivo, ingredienteId);
    }

    private void validarSessao() {
        if (restauranteIdAtivo == -1 || estacaoIdAtiva == -1) {
            throw new IllegalStateException("Contexto de produção não selecionado.");
        }
    }
}
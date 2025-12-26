package dss2526.ui.controller;

import dss2526.domain.entity.*;
import dss2526.service.producao.IProducaoFacade;
import dss2526.service.producao.ProducaoFacade;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ProducaoController {
    private final IProducaoFacade facade;
    private int restauranteIdAtivo = -1;
    private int estacaoIdAtiva = -1;
    private LocalDateTime inicioSessao;

    public ProducaoController() {
        // Como ProducaoFacade estende BaseFacade, ela herda os métodos de listagem
        this.facade = ProducaoFacade.getInstance();
    }

    public void selecionarContexto(int restIdx, int estIdx) {
        // Usa o método listarRestaurantes() herdado da BaseFacade
        Restaurante r = ((ProducaoFacade) facade).listarRestaurantes().get(restIdx);
        
        // Usa o método listarEstacoesDeRestaurante(id) herdado da BaseFacade
        List<Estacao> estacoes = ((ProducaoFacade) facade).listarEstacoesDeRestaurante(r.getId());
        
        this.restauranteIdAtivo = r.getId();
        this.estacaoIdAtiva = estacoes.get(estIdx).getId();
        this.inicioSessao = LocalDateTime.now();
    }

    public List<Tarefa> getFilaTrabalho() {
        return facade.consultarTarefasEstacao(restauranteIdAtivo, estacaoIdAtiva);
    }

    public List<Mensagem> getMensagensNovas() {
        return facade.consultarMensagens(restauranteIdAtivo).stream()
                .filter(m -> m.getDataHora().isAfter(inicioSessao))
                .collect(Collectors.toList());
    }

    public void concluirTarefa(int tarefaId) {
        facade.concluirTarefa(tarefaId);
    }

    public void solicitarIngrediente(int ingId) {
        facade.registarAlertaStock(restauranteIdAtivo, ingId);
    }

    // --- Métodos de Apoio à UI usando a herança da BaseFacade ---

    public List<String> listarRestaurantes() {
        return ((ProducaoFacade) facade).listarRestaurantes().stream()
                .map(Restaurante::getNome)
                .collect(Collectors.toList());
    }

    public List<String> listarEstacoes(int restIdx) {
        Restaurante r = ((ProducaoFacade) facade).listarRestaurantes().get(restIdx);
        return ((ProducaoFacade) facade).listarEstacoesDeRestaurante(r.getId()).stream()
                .map(e -> "Estação #" + e.getId() + " [" + e.getTrabalho() + "]")
                .collect(Collectors.toList());
    }
}
package dss2526.ui.controller;

import dss2526.domain.entity.Mensagem;
import dss2526.domain.entity.Tarefa;
import dss2526.service.producao.IProducaoFacade;

import java.util.List;

public class ProducaoController {
    private final IProducaoFacade facade;
    
    // ESTADO DO TERMINAL (Sessão do Funcionário)
    private int restauranteIdAtivo = -1;
    private int estacaoIdAtiva = -1;

    public ProducaoController(IProducaoFacade facade) {
        this.facade = facade;
    }

    /**
     * Define o contexto de trabalho do funcionário.
     * Chamado logo no início da UI de Produção.
     */
    public void selecionarContexto(int restauranteId, int estacaoId) {
        this.restauranteIdAtivo = restauranteId;
        this.estacaoIdAtiva = estacaoId;
    }

    /**
     * Obtém a fila de tarefas filtrada para esta estação.
     * A cada chamada, a Facade verifica se há novos pedidos pendentes.
     */
    public List<Tarefa> getFilaTrabalho() {
        validarSessao();
        return facade.consultarTarefasEstacao(restauranteIdAtivo, estacaoIdAtiva);
    }

    /**
     * Obtém as mensagens da gestão, ordenadas da mais recente para a mais antiga.
     */
    public List<Mensagem> getMensagensGestao() {
        validarSessao();
        return facade.consultarMensagens(restauranteIdAtivo);
    }

    /**
     * Conclui uma tarefa específica.
     * Se for a última tarefa de um pedido, a Facade atualizará o estado do pedido.
     */
    public void concluirTarefa(int tarefaId) {
        validarSessao();
        facade.concluirTarefa(tarefaId);
    }

    /**
     * Permite ao funcionário sinalizar que um ingrediente está em falta.
     */
    public void solicitarIngrediente(int ingredienteId) {
        validarSessao();
        facade.registarAlertaStock(restauranteIdAtivo, ingredienteId);
    }

    /**
     * Limpa a sessão do terminal.
     */
    public void encerrarSessao() {
        this.restauranteIdAtivo = -1;
        this.estacaoIdAtiva = -1;
    }

    // --- Auxiliares ---

    private void validarSessao() {
        if (restauranteIdAtivo == -1 || estacaoIdAtiva == -1) {
            throw new IllegalStateException("Erro: O terminal não está associado a um Restaurante ou Estação.");
        }
    }

    public int getRestauranteIdAtivo() { return restauranteIdAtivo; }
    public int getEstacaoIdAtiva() { return estacaoIdAtiva; }
}
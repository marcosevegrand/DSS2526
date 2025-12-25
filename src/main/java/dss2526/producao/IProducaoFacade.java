package dss2526.producao;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.Trabalho;
import java.util.List;

public interface IProducaoFacade {
    // --- Comunicação e Mensagens ---
    // Agora recebe o ID do restaurante para onde a mensagem vai ou de onde vem
    void receberMensagemGerencia(Mensagem msg, int restauranteId);
    List<Mensagem> lerAvisosPendentes(int restauranteId);

    // --- Fluxo de Trabalho por Contexto ---
    void registarNovoPedido(Pedido pedido); // O pedido já traz o RestauranteID
    
    // Filtra tarefas pela Estação (aba selecionada) e pelo Restaurante (login)
    List<Tarefa> obterTarefas(int restauranteId, Trabalho tipoEstacao);
    
    void iniciarTarefa(int idTarefa);
    void concluirTarefa(int idTarefa);
    
    // --- Gestão de Stock e Atrasos ---
    // Ao reportar falta, o sistema deve calcular o atraso (ex: marcar tarefa como bloqueada)
    void reportarFaltaIngrediente(int idTarefa, int idIngrediente, int restauranteId);
    void reportarReabastecimento(int idIngrediente, int restauranteId);

    // Métodos extra para a UI
    List<Estacao> listarEstacoesPorRestaurante(int restauranteId);
}
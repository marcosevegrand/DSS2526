package dss2526.producao;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.Trabalho;
import java.util.List;

public interface IProducaoFacade {
    void registarNovoPedido(Pedido pedido); // Método em falta
    List<Tarefa> obterTarefas(int restauranteId, Trabalho tipo);
    void iniciarTarefa(int tarefaId);
    void concluirTarefa(int tarefaId);
    void difundirMensagem(Mensagem msg, int restauranteId);
    List<Mensagem> lerAvisosPendentes(int restauranteId);
    void atualizarStockLocal(int ingredienteId, int restauranteId, float quantidade);
    List<String> getAlertasStock(int restauranteId);
    void reportarFaltaIngrediente(int tarefaId, int ingredienteId, int restauranteId);
    List<Estacao> listarEstacoesPorRestaurante(int restauranteId);
}
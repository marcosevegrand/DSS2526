package dss2526.producao;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.Trabalho;
import java.util.List;

public interface IProducaoFacade {
    // Comunicação
    void receberMensagemGerencia(Mensagem msg);
    Mensagem lerProximoAviso();

    // Fluxo de Trabalho
    void registarNovoPedido(Pedido pedido); 
    List<Tarefa> obterTarefasPorEstacao(Trabalho estacao);
    
    void iniciarTarefa(int idTarefa);
    void concluirTarefa(int idTarefa);
    
    // Gestão de Stock e Alertas
    void reportarFaltaIngrediente(int idTarefa, int idIngrediente);
    void reportarReabastecimento(int idIngrediente); // Desbloqueia tarefas
}
package dss2526.service.gestao;

import dss2526.domain.entity.*;
import dss2526.service.base.IBaseFacade;
import java.time.LocalDateTime;
import java.util.List;

public interface IGestaoFacade extends IBaseFacade {
    Funcionario autenticarFuncionario(String user, String pass);
    
    // Gestão de Equipa
    void contratarFuncionario(int actorId, Funcionario novo);
    void demitirFuncionario(int actorId, int funcionarioId);
    List<Funcionario> listarFuncionariosPorRestaurante(int rId);
    List<Estacao> listarEstacoesPorRestaurante(int rId);

    // Gestão de Stock
    void atualizarStockIngrediente(int actorId, int restauranteId, int ingredienteId, int delta);
    
    // Dashboard
    String obterDashboardEstatisticas(int restauranteId, LocalDateTime inicio, LocalDateTime fim);
    
    // Gestão de Estações
    void adicionarEstacaoTrabalho(int actorId, Estacao e);
    void removerEstacaoTrabalho(int actorId, int estacaoId);
    
    // Comunicação
    void enviarMensagemRestaurante(int actorId, int restauranteId, String texto);
    void difundirMensagemGlobal(int actorId, String texto);
}
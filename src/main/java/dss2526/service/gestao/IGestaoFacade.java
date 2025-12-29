package dss2526.service.gestao;

import dss2526.domain.entity.*;
import dss2526.service.base.IBaseFacade;
import java.time.LocalDateTime;
import java.util.List;

public interface IGestaoFacade extends IBaseFacade {
    // Autenticação
    Funcionario autenticarFuncionario(String user, String pass);
    
    // Gestão de Equipa (Específico ao Restaurante)
    void contratarFuncionario(int restauranteId, Funcionario novo);
    void demitirFuncionario(int funcionarioId);
    List<Funcionario> listarFuncionariosPorRestaurante(int rId);
    
    // Gestão de Estações (Específico ao Restaurante)
    void adicionarEstacaoTrabalho(Estacao e);
    void removerEstacaoTrabalho(int estacaoId);
    List<Estacao> listarEstacoesPorRestaurante(int rId);

    // Gestão de Stock (Específico ao Restaurante)
    void atualizarStockIngrediente(int restauranteId, int ingredienteId, int delta);
    
    // Dashboard
    String obterDashboardEstatisticas(int restauranteId, LocalDateTime inicio, LocalDateTime fim);
    
    // Comunicação
    void enviarMensagemRestaurante(int restauranteId, String texto, String nomeAutor);
    void difundirMensagemGlobal(String texto, String nomeAutor);
    
    // Listagens Gerais
    List<Restaurante> listarRestaurantes();
    List<Ingrediente> listarIngredientes();
}
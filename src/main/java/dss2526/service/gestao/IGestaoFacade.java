package dss2526.service.gestao;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.Trabalho;
import java.util.List;

public interface IGestaoFacade {
    // Autenticação
    Funcionario login(String utilizador, String password);
    void logout();
    
    // Gestão de Restaurantes (Exclusivo COO)
    void criarRestaurante(Funcionario responsavel, Restaurante r);
    void removerRestaurante(Funcionario responsavel, int restauranteId);
    List<Restaurante> listarTodosRestaurantes(Funcionario responsavel);

    // Gestão de Unidade (Gerente no seu, COO em qualquer um)
    List<Funcionario> listarFuncionarios(Funcionario responsavel, int restauranteId);
    void contratarFuncionario(Funcionario responsavel, Funcionario novo);
    void demitirFuncionario(Funcionario responsavel, int funcionarioId);
    
    void adicionarEstacao(Funcionario responsavel, int restauranteId, Trabalho tipo);
    void removerEstacao(Funcionario responsavel, int estacaoId);

    // Stock e Mensagens
    void atualizarStock(Funcionario responsavel, int restauranteId, int ingredienteId, float quantidade);
    void enviarAvisoCozinha(Funcionario responsavel, int restauranteId, String texto, boolean urgente);

    // Estatísticas
    double consultarFaturacao(Funcionario responsavel, int restauranteId);
}
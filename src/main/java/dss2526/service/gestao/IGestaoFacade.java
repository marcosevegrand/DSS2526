package dss2526.service.gestao;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.Trabalho;
import java.util.List;

public interface IGestaoFacade {
    Funcionario login(String u, String p);
    void logout();
    
    // COO apenas
    void criarRestaurante(Funcionario responsavel, Restaurante r);
    void removerRestaurante(Funcionario responsavel, int id);
    List<Restaurante> listarTodosRestaurantes(Funcionario res);

    // Gerente e COO
    List<Funcionario> listarFuncionarios(Funcionario res, int rId);
    void contratarFuncionario(Funcionario res, Funcionario novo);
    void demitirFuncionario(Funcionario res, int fId);
    void adicionarEstacao(Funcionario res, int rId, Trabalho t);
    void removerEstacao(Funcionario res, int eId);
    
    // Comunicação e Estatísticas (via BD)
    void atualizarStock(Funcionario res, int rId, int iId, float qtd);
    double consultarFaturacao(Funcionario res, int rId);
    void enviarAvisoCozinha(Funcionario res, int rId, String txt, boolean urg);
}
package dss2526.service.gestao;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.Trabalho;
import dss2526.service.base.IBaseFacade;
import java.util.Map;

public interface IGestaoFacade extends IBaseFacade {

    // --- Autenticação ---
    Funcionario login(String user, String password);

    // --- Gestão Global (Apenas COO) ---
    Restaurante criarRestaurante(Funcionario actor, String nome, String localizacao);
    void removerRestaurante(Funcionario actor, int id);
    
    Produto criarProduto(Funcionario actor, Produto p);
    Menu criarMenu(Funcionario actor, Menu m);
    Ingrediente criarIngrediente(Funcionario actor, Ingrediente i);
    Passo criarPasso(Funcionario actor, Passo p); // NOVO
    Catalogo criarCatalogo(Funcionario actor, String nome);
    
    // --- Gestão Local (COO ou Gerente do Restaurante) ---
    void contratarFuncionario(Funcionario actor, Funcionario novo);
    void demitirFuncionario(Funcionario actor, int funcionarioId);
    void adicionarEstacao(Funcionario actor, int restauranteId, Trabalho trabalho);
    void removerEstacao(Funcionario actor, int estacaoId);
    void alterarCatalogoRestaurante(Funcionario actor, int restauranteId, int catalogoId); // NOVO
    
    // --- Gestão Operacional (COO, Gerente ou Funcionario do Restaurante) ---
    void atualizarStock(Funcionario actor, int restauranteId, int ingredienteId, int quantidade);
    
    // --- Estatísticas e Consultas ---
    double consultarFaturacaoTotal(Funcionario actor, int restauranteId);
    Map<String, Integer> consultarProdutosMaisVendidos(Funcionario actor, int restauranteId);
    void enviarAvisoCozinha(Funcionario actor, int restauranteId, String mensagem, boolean urgente);
}
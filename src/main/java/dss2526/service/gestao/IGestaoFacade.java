package dss2526.service.gestao;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.Trabalho;
import dss2526.service.base.IBaseFacade;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IGestaoFacade extends IBaseFacade {
    // Autenticacao
    Funcionario login(String utilizador, String password);

    // Gestao Global (COO)
    void criarRestaurante(int actorId, String nome, String localizacao);
    void criarIngrediente(int actorId, String nome, String unidade, String alergenico);
    void criarProduto(int actorId, String nome, double preco, List<Integer> passos, Map<Integer, Integer> receita);
    void criarCatalogo(int actorId, String nome, List<Integer> produtos, List<Integer> menus);

    // Gestao Local (Gerente ou COO)
    void contratarFuncionario(int actorId, int restauranteId, Funcionario novo);
    void demitirFuncionario(int actorId, int funcionarioId);
    void atualizarStock(int actorId, int restauranteId, int ingredienteId, int quantidade);
    void configurarEstacao(int actorId, int restauranteId, String nome, Trabalho trabalho);

    // Estatisticas com Filtro Temporal
    double consultarFaturacao(int restauranteId, LocalDateTime inicio, LocalDateTime fim);
    Map<String, Integer> consultarTopProdutos(int restauranteId, LocalDateTime inicio, LocalDateTime fim);
    double consultarTempoMedioEspera(int restauranteId, LocalDateTime inicio, LocalDateTime fim);
    Map<String, Long> consultarVolumePedidos(int restauranteId, LocalDateTime inicio, LocalDateTime fim);
}
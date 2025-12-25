package dss2526.gestao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import dss2526.domain.entity.Estacao;
import dss2526.domain.entity.Funcionario;
import dss2526.domain.entity.Restaurante;
import dss2526.domain.enumeration.RoleTrabalhador;
import dss2526.domain.enumeration.Trabalho;

import java.util.List;

public interface IGestaoFacade {
        
    BigDecimal calcularFaturacao(LocalDate data, Integer idRestaurante);
    
    Map<String, Integer> obterTopProdutosVendidos(Integer idRestaurante);
    
    double calcularTempoMedioEspera(LocalDate data, Integer idRestaurante);
    
    List<String> verificarProdutosAbaixoDoStock(Integer idRestaurante);
    
    void reporStockIngrediente(int ingredienteId, int restauranteId, float quantidade);

    void enviarMensagemIncentivo(String texto, Integer alvoRestauranteId);

    int criarRestaurante(String nome, String localizacao);

    void adicionarEstacaotrabalho(int restauranteId, Trabalho tipo);
    
    void registarFuncionario(int restauranteId, String nome, String user, String pass, RoleTrabalhador papel);
    
    void configurarNovoRestaurante(Restaurante r, List<Estacao> estacoes, List<Funcionario> funcionarios);
        
    List<String> listarNomesRestaurantes();
}
package dss2526.gestao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.List;

import dss2526.domain.entity.Estacao;
import dss2526.domain.entity.Funcionario;
import dss2526.domain.entity.Restaurante;
import dss2526.domain.enumeration.RoleTrabalhador;
import dss2526.domain.enumeration.Trabalho;

/**
 * Interface de fachada sincronizada com a GestaoUI e GestaoFacade.
 */
public interface IGestaoFacade {
        
    // --- Métodos Chamados pela GestaoUI ---
    
    int registarRestaurante(String nome, String localizacao);

    void adicionarEstacao(int restauranteId, Trabalho tipo);
    
    void contratarFuncionario(String nome, String user, String pass, RoleTrabalhador papel, int resId);

    void enviarMensagemProducao(int resId, String texto, boolean urgente);

    List<String> getAlertasStock(int restauranteId);

    void atualizarStockLocal(int ingredienteId, int restauranteId, float quantidade);

    // --- Estatísticas e Relatórios ---
    
    BigDecimal calcularFaturacao(LocalDate data, int idRestaurante);
    
    Map<String, Integer> obterTopProdutosVendidos(int idRestaurante);
    
    double calcularTempoMedioEspera(LocalDate data, int idRestaurante);
    
    // --- Configuração e Listagens ---

    void configurarNovoRestaurante(Restaurante r, List<Estacao> estacoes, List<Funcionario> funcionarios);
        
    List<String> listarNomesRestaurantes();
}
package dss2526.gestao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.List;

public interface IGestaoFacade {
    
    // --- Estatísticas Integradas (Cenário 4) ---
    
    BigDecimal calcularFaturacao(LocalDate data, Integer idRestaurante);
    
    Map<String, Integer> obterTopProdutosVendidos(Integer idRestaurante);
    
    double calcularTempoMedioEspera(LocalDate data, Integer idRestaurante);
    
    List<String> verificarProdutosAbaixoDoStock(Integer idRestaurante);
    
    void reporStockIngrediente(int ingredienteId, int restauranteId, float quantidade);

    void enviarMensagemIncentivo(String texto, Integer alvoRestauranteId);
        
    List<String> listarNomesRestaurantes();
}
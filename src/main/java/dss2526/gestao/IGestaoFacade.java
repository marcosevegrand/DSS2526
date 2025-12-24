package dss2526.gestao;

import dss2526.domain.entity.Mensagem;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public interface IGestaoFacade {
    // Estatísticas
    BigDecimal calcularFaturacaoDoDia(LocalDate data);
    Map<String, Integer> obterTopProdutosVendidos();
    BigDecimal calcularTicketMedio();

    // Comunicação com Produção
    void enviarAvisoProducao(String texto, boolean prioritario);

    void reporStockIngrediente(int ingredienteId); 
}
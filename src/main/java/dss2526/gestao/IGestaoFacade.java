package dss2526.gestao;

import java.time.LocalDate;
import java.util.Map;

public interface IGestaoFacade {
    
    /**
     * Calcula a faturação total num período de tempo.
     * 
     * @param dataInicio data de início do período
     * @param dataFim data de fim do período
     * @return faturação total em euros
     */
    float calcularFaturacaoTotal(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Conta o número de pedidos finalizados (pagos) num período.
     * 
     * @param dataInicio data de início do período
     * @param dataFim data de fim do período
     * @return número de pedidos finalizados
     */
    int contarPedidosFinalizados(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Calcula o ticket médio (valor médio por pedido) num período.
     * 
     * @param dataInicio data de início do período
     * @param dataFim data de fim do período
     * @return valor médio por pedido
     */
    float calcularTicketMedio(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Obtém os produtos/menus mais vendidos num período.
     * 
     * @param dataInicio data de início do período
     * @param dataFim data de fim do período
     * @param top número de produtos a retornar (top N)
     * @return mapa com ID do item e quantidade vendida, ordenado por quantidade (desc)
     */
    Map<Integer, Integer> obterProdutosMaisVendidos(
        LocalDate dataInicio,
        LocalDate dataFim,
        int top
    );

    /**
     * Calcula o tempo médio de espera estimado dos pedidos num período.
     * 
     * @param dataInicio data de início do período
     * @param dataFim data de fim do período
     * @return tempo médio em minutos
     */
    int calcularTempoMedioEspera(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Conta pedidos por modo de consumo (LOCAL vs TAKE_AWAY) num período.
     * 
     * @param dataInicio data de início do período
     * @param dataFim data de fim do período
     * @return mapa com modo de consumo e contagem
     */
    Map<String, Integer> contarPedidosPorModoConsumo(
        LocalDate dataInicio,
        LocalDate dataFim
    );

    /**
     * Calcula a taxa de cancelamento de pedidos num período.
     * 
     * @param dataInicio data de início do período
     * @param dataFim data de fim do período
     * @return percentagem de pedidos cancelados
     */
    float calcularTaxaCancelamento(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Obtém a faturação diária num período.
     * 
     * @param dataInicio data de início do período
     * @param dataFim data de fim do período
     * @return mapa com data e faturação do dia
     */
    Map<LocalDate, Float> obterFaturacaoPorDia(
        LocalDate dataInicio,
        LocalDate dataFim
    );
}

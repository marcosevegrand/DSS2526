package pt.uminho.dss.restaurante.ui.controller;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

import pt.uminho.dss.restaurante.estatistica.IEstatistica;

/**
 * Controller para a UI de estatísticas.
 */
public class EstatisticaController {

    private final IEstatistica estatistica;

    public EstatisticaController(IEstatistica estatistica) {
        this.estatistica = Objects.requireNonNull(estatistica);
    }

    public float faturacaoTotal(LocalDate inicio, LocalDate fim) {
        return estatistica.calcularFaturacaoTotal(inicio, fim);
    }

    public float ticketMedio(LocalDate inicio, LocalDate fim) {
        return estatistica.calcularTicketMedio(inicio, fim);
    }

    public float taxaCancelamento(LocalDate inicio, LocalDate fim) {
        return estatistica.calcularTaxaCancelamento(inicio, fim);
    }

    public Map<Integer, Integer> topProdutos(LocalDate inicio, LocalDate fim, int top) {
        return estatistica.obterProdutosMaisVendidos(inicio, fim, top);
    }

    public Map<LocalDate, Float> faturacaoPorDia(LocalDate inicio, LocalDate fim) {
        return estatistica.obterFaturacaoPorDia(inicio, fim);
    }
}
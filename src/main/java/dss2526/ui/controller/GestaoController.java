package dss2526.ui.controller;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

/**
 * Controlador para o Dashboard de Gestão.
 */
public class GestaoController {
    // Retorna valores fixos para os gráficos
    public float faturacaoTotal(LocalDate i, LocalDate f) { return 1250.0f; }
    public float ticketMedio(LocalDate i, LocalDate f) { return 15.5f; }
    public float taxaCancelamento(LocalDate i, LocalDate f) { return 2.5f; }
    public Map<Integer, Integer> topProdutos(LocalDate i, LocalDate f, int t) { return new HashMap<>(); }
    public Map<LocalDate, Float> faturacaoPorDia(LocalDate i, LocalDate f) { return new HashMap<>(); }
}
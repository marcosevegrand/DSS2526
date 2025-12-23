package pt.uminho.dss.restaurante.estatistica;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Objects;

import pt.uminho.dss.restaurante.persistence.contract.PedidoDAO;
import pt.uminho.dss.restaurante.domain.entity.Pedido;
import pt.uminho.dss.restaurante.domain.entity.LinhaPedido;
import pt.uminho.dss.restaurante.domain.enumeration.EstadoPedido;

/**
 * Implementação simples de IEstatistica baseada em PedidoDAO.
 * Filtra por data de pagamento (dataHoraPagamento) quando apropriado.
 */
public class EstatisticaFacade implements IEstatistica {

    private final PedidoDAO pedidoDAO;

    public EstatisticaFacade(PedidoDAO pedidoDAO) {
        this.pedidoDAO = Objects.requireNonNull(pedidoDAO);
    }

    private List<Pedido> obterPedidosPagosNoPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null)
            return Collections.emptyList();
        return pedidoDAO.values().stream()
                .filter(p -> p.getDataHora() != null && p.getEstado() == EstadoPedido.PAGO) // Use getDataHora and check
                                                                                            // status
                .filter(p -> {
                    LocalDate d = p.getDataHora().toLocalDate();
                    return (!d.isBefore(inicio)) && (!d.isAfter(fim));
                })
                .collect(Collectors.toList());
    }

    @Override
    public float calcularFaturacaoTotal(LocalDate dataInicio, LocalDate dataFim) {
        return (float) obterPedidosPagosNoPeriodo(dataInicio, dataFim).stream()
                .map(Pedido::calcularPrecoTotal) // Method ref change
                .mapToDouble(java.math.BigDecimal::doubleValue)
                .sum();
    }

    @Override
    public int contarPedidosFinalizados(LocalDate dataInicio, LocalDate dataFim) {
        return obterPedidosPagosNoPeriodo(dataInicio, dataFim).size();
    }

    @Override
    public float calcularTicketMedio(LocalDate dataInicio, LocalDate dataFim) {
        List<Pedido> pedidos = obterPedidosPagosNoPeriodo(dataInicio, dataFim);
        if (pedidos.isEmpty())
            return 0f;
        float total = (float) pedidos.stream().map(Pedido::calcularPrecoTotal)
                .mapToDouble(java.math.BigDecimal::doubleValue).sum();
        return total / pedidos.size();
    }

    @Override
    public Map<Integer, Integer> obterProdutosMaisVendidos(LocalDate dataInicio, LocalDate dataFim, int top) {
        Map<Integer, Integer> contagem = new HashMap<>(); // Same logic ...
        List<Pedido> pedidos = obterPedidosPagosNoPeriodo(dataInicio, dataFim);
        for (Pedido p : pedidos) {
            for (LinhaPedido lp : p.getLinhasPedido()) { // getLinhas vs getLinhasPedido
                Integer id = lp.getItem().getId(); //
                if (id == null)
                    continue;
                contagem.merge(id, lp.getQuantidade(), Integer::sum);
            }
        }
        return contagem.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(top)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    @Override
    public int calcularTempoMedioEspera(LocalDate dataInicio, LocalDate dataFim) {
        // Pedido doesn't have estimated wait time. Returning 0.
        return 0;
    }

    @Override
    public Map<String, Integer> contarPedidosPorModoConsumo(LocalDate dataInicio, LocalDate dataFim) {
        List<Pedido> pedidos = obterPedidosPagosNoPeriodo(dataInicio, dataFim);
        Map<String, Integer> mapa = new HashMap<>();
        for (Pedido p : pedidos) {
            String modo = p.isParaLevar() ? "TAKE_AWAY" : "EAT_IN";
            mapa.merge(modo, 1, Integer::sum);
        }
        return mapa;
    }

    @Override
    public float calcularTaxaCancelamento(LocalDate dataInicio, LocalDate dataFim) {
        List<Pedido> todosNoPeriodo = pedidoDAO.values().stream()
                .filter(p -> p.getDataHora() != null)
                .filter(p -> {
                    LocalDate d = p.getDataHora().toLocalDate();
                    return (!d.isBefore(dataInicio)) && (!d.isAfter(dataFim));
                }).collect(Collectors.toList());
        if (todosNoPeriodo.isEmpty())
            return 0f;
        long cancelados = todosNoPeriodo.stream().filter(p -> p.getEstado() == EstadoPedido.CANCELADO).count();
        return (float) cancelados / todosNoPeriodo.size() * 100f;
    }

    @Override
    public Map<LocalDate, Float> obterFaturacaoPorDia(LocalDate dataInicio, LocalDate dataFim) {
        Map<LocalDate, Float> mapa = new LinkedHashMap<>();
        for (LocalDate cur = dataInicio; !cur.isAfter(dataFim); cur = cur.plusDays(1)) {
            final LocalDate data = cur;
            float f = (float) pedidoDAO.values().stream()
                    .filter(p -> p.getDataHora() != null && p.getEstado() == EstadoPedido.PAGO)
                    .filter(p -> p.getDataHora().toLocalDate().equals(data))
                    .map(Pedido::calcularPrecoTotal)
                    .mapToDouble(java.math.BigDecimal::doubleValue)
                    .sum();
            mapa.put(data, f);
        }
        return mapa;
    }
}
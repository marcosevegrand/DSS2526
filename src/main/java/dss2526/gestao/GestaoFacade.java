package dss2526.gestao;

import dss2526.data.contract.PedidoDAO;
import dss2526.domain.entity.Pedido;
import dss2526.domain.entity.LinhaPedido;
import dss2526.domain.enumeration.EstadoPedido;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GestaoFacade implements IGestaoFacade {

    private final PedidoDAO pedidoDAO;

    public GestaoFacade(PedidoDAO pedidoDAO) {
        this.pedidoDAO = pedidoDAO;
    }

    @Override
    public float calcularFaturacaoTotal(LocalDate dataInicio, LocalDate dataFim) {
        List<Pedido> pedidos = getPedidosInRange(dataInicio, dataFim);
        return pedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.ENTREGUE)
                .map(Pedido::calcularPrecoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .floatValue();
    }

    @Override
    public int contarPedidosFinalizados(LocalDate dataInicio, LocalDate dataFim) {
        List<Pedido> pedidos = getPedidosInRange(dataInicio, dataFim);
        return (int) pedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.ENTREGUE)
                .count();
    }

    @Override
    public float calcularTicketMedio(LocalDate dataInicio, LocalDate dataFim) {
        List<Pedido> pedidos = getPedidosInRange(dataInicio, dataFim).stream()
                .filter(p -> p.getEstado() == EstadoPedido.ENTREGUE)
                .toList();

        if (pedidos.isEmpty()) {
            return 0;
        }

        BigDecimal total = pedidos.stream()
                .map(Pedido::calcularPrecoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.divide(BigDecimal.valueOf(pedidos.size()), BigDecimal.ROUND_HALF_UP).floatValue();
    }

    @Override
    public Map<Integer, Integer> obterProdutosMaisVendidos(LocalDate dataInicio, LocalDate dataFim, int top) {
        List<Pedido> pedidos = getPedidosInRange(dataInicio, dataFim);
        
        return pedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.ENTREGUE)
                .flatMap(p -> p.getLinhasPedido().stream())
                .collect(Collectors.groupingBy(
                        lp -> lp.getItem().hashCode(), // Use appropriate ID
                        Collectors.summingInt(LinhaPedido::getQuantidade)
                ))
                .entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(top)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public int calcularTempoMedioEspera(LocalDate dataInicio, LocalDate dataFim) {
        // Placeholder implementation - requires timestamp data
        return 15; // Return default 15 minutes
    }

    @Override
    public Map<String, Integer> contarPedidosPorModoConsumo(LocalDate dataInicio, LocalDate dataFim) {
        List<Pedido> pedidos = getPedidosInRange(dataInicio, dataFim);
        
        Map<String, Integer> resultado = new HashMap<>();
        int local = 0;
        int takeAway = 0;

        for (Pedido p : pedidos) {
            if (p.isParaLevar()) {
                takeAway++;
            } else {
                local++;
            }
        }

        resultado.put("LOCAL", local);
        resultado.put("TAKE_AWAY", takeAway);
        return resultado;
    }

    @Override
    public float calcularTaxaCancelamento(LocalDate dataInicio, LocalDate dataFim) {
        List<Pedido> pedidos = getPedidosInRange(dataInicio, dataFim);
        
        if (pedidos.isEmpty()) {
            return 0;
        }

        long cancelados = pedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.CANCELADO)
                .count();

        return (float) (cancelados * 100.0 / pedidos.size());
    }

    @Override
    public Map<LocalDate, Float> obterFaturacaoPorDia(LocalDate dataInicio, LocalDate dataFim) {
        Map<LocalDate, Float> faturacaoDiaria = new HashMap<>();
        LocalDate currentDate = dataInicio;

        while (!currentDate.isAfter(dataFim)) {
            List<Pedido> pedidosDia = pedidoDAO.findByData(currentDate);
            
            float faturacaoDia = pedidosDia.stream()
                    .filter(p -> p.getEstado() == EstadoPedido.ENTREGUE)
                    .map(Pedido::calcularPrecoTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .floatValue();

            faturacaoDiaria.put(currentDate, faturacaoDia);
            currentDate = currentDate.plusDays(1);
        }

        return faturacaoDiaria;
    }

    private List<Pedido> getPedidosInRange(LocalDate dataInicio, LocalDate dataFim) {
        List<Pedido> pedidos = new ArrayList<>();
        LocalDate currentDate = dataInicio;

        while (!currentDate.isAfter(dataFim)) {
            pedidos.addAll(pedidoDAO.findByData(currentDate));
            currentDate = currentDate.plusDays(1);
        }

        return pedidos;
    }
}

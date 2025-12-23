package pt.uminho.dss.restaurante.estatistica;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
// import pt.uminho.dss.restaurante.core.domain.entity.LinhaPedido;
import pt.uminho.dss.restaurante.core.domain.entity.Pedido;
import pt.uminho.dss.restaurante.core.domain.enumeration.EstadoPedido;
import pt.uminho.dss.restaurante.persistence.contract.PedidoDAO;

public class EstatisticaFacade implements IEstatistica {

    private final PedidoDAO pedidoDAO;

    public EstatisticaFacade(PedidoDAO pedidoDAO) {
        this.pedidoDAO = pedidoDAO;
    }

    @Override
    public float calcularFaturacaoTotal(LocalDate dataInicio, LocalDate dataFim) {
        List<Pedido> pedidos = obterPedidosPagosNoPeriodo(dataInicio, dataFim);
        return pedidos.stream()
            .map(Pedido::getPrecoTotal)
            .reduce(0f, Float::sum);
    }

    @Override
    public int contarPedidosFinalizados(LocalDate dataInicio, LocalDate dataFim) {
        List<Pedido> pedidos = obterPedidosPagosNoPeriodo(dataInicio, dataFim);
        return pedidos.size();
    }

    @Override
    public float calcularTicketMedio(LocalDate dataInicio, LocalDate dataFim) {
        List<Pedido> pedidos = obterPedidosPagosNoPeriodo(dataInicio, dataFim);
        if (pedidos.isEmpty()) {
            return 0f;
        }
        float total = pedidos.stream()
            .map(Pedido::getPrecoTotal)
            .reduce(0f, Float::sum);
        return total / pedidos.size();
    }

    @Override
    public Map<Integer, Integer> obterProdutosMaisVendidos(
        LocalDate dataInicio,
        LocalDate dataFim,
        int top
    ) {
        // List<Pedido> pedidos = obterPedidosPagosNoPeriodo(dataInicio, dataFim);
        Map<Integer, Integer> contagemPorItem = new HashMap<>();

        // for (Pedido p : pedidos) {
        //     for (LinhaPedido linha : p.getLinhas()) {
        //         int idItem = linha.getItem().getId();
        //         int quantidade = linha.getQuantidade();
        //         contagemPorItem.merge(idItem, quantidade, Integer::sum);
        //     }
        // }

        return contagemPorItem.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(top)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                java.util.LinkedHashMap::new
            ));
    }

    @Override
    public int calcularTempoMedioEspera(LocalDate dataInicio, LocalDate dataFim) {
        List<Pedido> pedidos = obterPedidosPagosNoPeriodo(dataInicio, dataFim);
        if (pedidos.isEmpty()) {
            return 0;
        }
        int tempoTotal = pedidos.stream()
            .mapToInt(Pedido::getTempoEsperaEstimado)
            .sum();
        return tempoTotal / pedidos.size();
    }

    @Override
    public Map<String, Integer> contarPedidosPorModoConsumo(
        LocalDate dataInicio,
        LocalDate dataFim
    ) {
        List<Pedido> pedidos = obterPedidosPagosNoPeriodo(dataInicio, dataFim);
        Map<String, Integer> contagem = new HashMap<>();

        for (Pedido p : pedidos) {
            String modo = p.getModoConsumo().toString();
            contagem.merge(modo, 1, Integer::sum);
        }

        return contagem;
    }

    @Override
    public float calcularTaxaCancelamento(LocalDate dataInicio, LocalDate dataFim) {
        List<Pedido> todosPedidos = pedidoDAO.findByData(dataInicio);
        
        // Filtrar pedidos do período
        todosPedidos = todosPedidos.stream()
            .filter(p -> {
                LocalDate dataPedido = p.getDataHoraCriacao().toLocalDate();
                return !dataPedido.isBefore(dataInicio) && !dataPedido.isAfter(dataFim);
            })
            .collect(Collectors.toList());

        if (todosPedidos.isEmpty()) {
            return 0f;
        }

        long cancelados = todosPedidos.stream()
            .filter(p -> p.getEstado() == EstadoPedido.CANCELADO)
            .count();

        return (float) cancelados / todosPedidos.size() * 100;
    }

    @Override
    public Map<LocalDate, Float> obterFaturacaoPorDia(
        LocalDate dataInicio,
        LocalDate dataFim
    ) {
        Map<LocalDate, Float> faturacaoPorDia = new HashMap<>();
        
        LocalDate dataAtual = dataInicio;
        while (!dataAtual.isAfter(dataFim)) {
            List<Pedido> pedidosDoDia = pedidoDAO.findByData(dataAtual).stream()
                .filter(p -> p.getEstado() == EstadoPedido.PAGO)
                .collect(Collectors.toList());

            float faturacao = pedidosDoDia.stream()
                .map(Pedido::getPrecoTotal)
                .reduce(0f, Float::sum);

            faturacaoPorDia.put(dataAtual, faturacao);
            dataAtual = dataAtual.plusDays(1);
        }

        return faturacaoPorDia;
    }

    // Método auxiliar privado
    private List<Pedido> obterPedidosPagosNoPeriodo(
        LocalDate dataInicio,
        LocalDate dataFim
    ) {
        List<Pedido> pedidos = pedidoDAO.findByEstado(EstadoPedido.PAGO);
        
        return pedidos.stream()
            .filter(p -> {
                LocalDateTime dataPagamento = p.getDataHoraPagamento();
                if (dataPagamento == null) return false;
                
                LocalDate dataPag = dataPagamento.toLocalDate();
                return !dataPag.isBefore(dataInicio) && !dataPag.isAfter(dataFim);
            })
            .collect(Collectors.toList());
    }
}

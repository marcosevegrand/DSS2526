package dss2526.gestao;

import dss2526.data.contract.*;
import dss2526.domain.entity.*;
import dss2526.producao.IProducaoFacade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

/**
 * Implementação da Facade de Gestão.
 * Responsável por estatísticas financeiras, relatórios de vendas
 * e comunicação de ordens da gerência para a produção.
 */
public class GestaoFacade implements IGestaoFacade {

    private final PedidoDAO pedidoDAO;
    private final ProdutoDAO produtoDAO;
    private final IProducaoFacade producaoFacade;

    public GestaoFacade(PedidoDAO pedidoDAO, 
                        ProdutoDAO produtoDAO, 
                        IProducaoFacade producaoFacade) {
        this.pedidoDAO = pedidoDAO;
        this.produtoDAO = produtoDAO;
        this.producaoFacade = producaoFacade;
    }

    /**
     * Calcula o total faturado num dia específico.
     */
    @Override
    public BigDecimal calcularFaturacaoDoDia(LocalDate data) {
        return pedidoDAO.findByData(data).stream()
                .map(Pedido::calcularPrecoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Gera um mapa com o nome dos produtos e a quantidade total vendida.
     */
    @Override
    public Map<String, Integer> obterTopProdutosVendidos() {
        Map<Integer, Integer> contagem = new HashMap<>();

        // Iterar por todos os pedidos para contar volumes
        for (Pedido p : pedidoDAO.values()) {
            for (LinhaPedido lp : p.getLinhasPedido()) {
                int id = lp.getItem().getId();
                contagem.put(id, contagem.getOrDefault(id, 0) + lp.getQuantidade());
            }
        }

        // Converter IDs em Nomes para apresentação na UI
        Map<String, Integer> resultado = new HashMap<>();
        contagem.forEach((id, qtd) -> {
            Produto prod = produtoDAO.get(id);
            String nome = (prod != null) ? prod.getNome() : "Item #" + id;
            resultado.put(nome, qtd);
        });

        return resultado;
    }

    /**
     * Calcula o valor médio gasto por pedido (Ticket Médio).
     */
    @Override
    public BigDecimal calcularTicketMedio() {
        List<Pedido> todos = pedidoDAO.values();
        if (todos.isEmpty()) return BigDecimal.ZERO;

        BigDecimal total = todos.stream()
                .map(Pedido::calcularPrecoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.divide(new BigDecimal(todos.size()), 2, RoundingMode.HALF_UP);
    }

    @Override
    public void enviarAvisoProducao(String texto, boolean prioritario) {
        Mensagem msg = new Mensagem(texto, prioritario);
        // Delegar à produção a gestão da entrega desta mensagem em memória
        producaoFacade.receberMensagemGerencia(msg);
    }

    @Override
    public void reporStockIngrediente(int ingredienteId) {
        producaoFacade.reportarReabastecimento(ingredienteId);
        
        String aviso = "Stock reposto (ID #" + ingredienteId + "). Podem retomar os pedidos em atraso.";
        enviarAvisoProducao(aviso, false);
    }
}
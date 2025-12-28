package dss2526.service.venda;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;
import dss2526.service.base.BaseFacade;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Facade STATELESS.
 * Implementação da lógica de negócio para o subsistema de Venda.
 */
public class VendaFacade extends BaseFacade implements IVendaFacade {

    private static VendaFacade instance;

    private VendaFacade() {}

    public static synchronized VendaFacade getInstance() {
        if (instance == null) {
            instance = new VendaFacade();
        }
        return instance;
    }

    @Override
    public Pedido iniciarPedido(Restaurante restaurante, Boolean paraLevar) {
        Pedido p = new Pedido();
        p.setRestauranteId(restaurante.getId());
        p.setParaLevar(paraLevar);
        p.setEstado(EstadoPedido.INICIADO);
        p.setDataCriacao(LocalDateTime.now());
        // Persistir o pedido inicialmente conforme a arquitetura do sistema
        return registarPedido(p);
    }

    @Override
    public void cancelarPedido(Pedido pedido) {
        if (pedido != null && pedido.getId() != 0) {
            // Remove o pedido da base de dados (ou poderia marcar como CANCELADO)
            pedido.setEstado(EstadoPedido.CANCELADO);
            atualizarPedido(pedido);
        }
    }

    @Override
    public List<Produto> listarProdutosDisponiveis(Restaurante restaurante, List<String> alergenicos) {
        Catalogo catalogo = obterCatalogo(restaurante.getCatalogoId());
        if (catalogo == null) return List.of();

        return catalogo.getProdutoIds().stream()
                .map(this::obterProduto)
                .filter(p -> p != null)
                .filter(p -> !contemAlergenicos(p, alergenicos))
                .filter(p -> verificarStockProduto(p, restaurante))
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> listarMenusDisponiveis(Restaurante restaurante, List<String> alergenicos) {
        Catalogo catalogo = obterCatalogo(restaurante.getCatalogoId());
        if (catalogo == null) return List.of();

        return catalogo.getMenuIds().stream()
                .map(this::obterMenu)
                .filter(m -> m != null)
                .filter(m -> m.getLinhas().stream().allMatch(lm -> {
                        Produto p = obterProduto(lm.getProdutoId());
                        return p != null && !contemAlergenicos(p, alergenicos) && verificarStockProduto(p, restaurante);
                    }))
                .collect(Collectors.toList());
    }

    @Override
    public Pedido adicionarLinhaAoPedido(Pedido pedido, LinhaPedido linha) {
        pedido.addLinha(linha);
        return atualizarPedido(pedido);
    }

    @Override
    public Pedido removerLinhaDoPedido(Pedido pedido, int index) {
        if (index >= 0 && index < pedido.getLinhas().size()) {
            LinhaPedido lp = pedido.getLinhas().get(index);
            pedido.removeLinha(lp);
            return atualizarPedido(pedido);
        }
        return pedido;
    }

    @Override
    public double finalizarPedido(Pedido pedido) {
        pedido.setEstado(EstadoPedido.CONFIRMADO);
        atualizarPedido(pedido);
        return calcularEstimativaTempo(pedido);
    }

    private double calcularEstimativaTempo(Pedido p) {
        double maxMinutos = 0.0;
        for (LinhaPedido lp : p.getLinhas()) {
            double tempoItem = 0.0;
            if (lp.getTipo() == TipoItem.PRODUTO) {
                tempoItem = calcularTempoProduto(lp.getItemId());
            } else {
                Menu m = menuDAO.findById(lp.getItemId());
                if (m != null) {
                    tempoItem = m.getLinhas().stream()
                            .mapToDouble(lm -> calcularTempoProduto(lm.getProdutoId()))
                            .max().orElse(0.0);
                }
            }
            if (tempoItem > maxMinutos) maxMinutos = tempoItem;
        }
        return maxMinutos > 0 ? maxMinutos + 2.0 : 0.0;
    }

    private double calcularTempoProduto(int produtoId) {
        Produto prod = produtoDAO.findById(produtoId);
        if (prod == null) return 0.0;
        return prod.getPassoIds().stream()
                .map(passoDAO::findById)
                .filter(Objects::nonNull)
                .mapToDouble(passo -> passo.getDuracao() != null ? passo.getDuracao().toSeconds() / 60.0 : 0.0)
                .sum();
    }

    private boolean contemAlergenicos(Produto p, List<String> alergenicosEvitar) {
        if (alergenicosEvitar == null || alergenicosEvitar.isEmpty()) return false;
        List<Ingrediente> ingredientes = p.getLinhas().stream()
                .map(lp -> obterIngrediente(lp.getIngredienteId()))
                .collect(Collectors.toList());
        for (Ingrediente ing : ingredientes) {
            if (ing != null && alergenicosEvitar.contains(ing.getAlergenico())) return true;
        }
        return false;
    }

    private boolean verificarStockProduto(Produto p, Restaurante r) {
        for (LinhaProduto comp : p.getLinhas()) {
            Integer ingredienteId = comp.getIngredienteId();
            Integer stockDisponivel = r.getStock().stream()
                    .filter(s -> s.getIngredienteId() == ingredienteId)
                    .map(LinhaStock::getQuantidade)
                    .findFirst().orElse(0);
            if (stockDisponivel < comp.getQuantidade()) return false; 
        }
        return true;
    }
}
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
 * Contém a lógica de negócio (Regras), mas não guarda dados da sessão.
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
        p.setDataHora(LocalDateTime.now());
        // Persistir o pedido inicialmente
        return registarPedido(p);
    }

    @Override
    public List<Produto> listarProdutosDisponiveis(Restaurante restaurante, List<String> alergenicos) {
        // 1. Obter catálogo
        Catalogo catalogo = obterCatalogo(restaurante.getCatalogoId());
        if (catalogo == null) return List.of();

        // 2. Obter produtos e filtrar
        return catalogo.getProdutoIds().stream()
                .map(this::obterProduto) // BaseFacade
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
                .filter(m -> {
                    // Um menu está disponível se todos os seus produtos estiverem disponíveis
                    return m.getLinhas().stream().allMatch(lm -> {
                        Produto p = obterProduto(lm.getProdutoId());
                        return p != null && 
                               !contemAlergenicos(p, alergenicos) && 
                               verificarStockProduto(p, restaurante);
                    });
                })
                .collect(Collectors.toList());
    }

    @Override
    public Pedido adicionarLinhaAoPedido(Pedido pedido, LinhaPedido linha) {
        // Regra de negócio: Atualizar preço ou validar novamente se necessário
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
        pedido.setDataHora(LocalDateTime.now());
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
                // Se for Menu, assume-se que o tempo é determinado pelo produto mais demorado do menu
                // ou pela soma dos produtos (depende da lógica, aqui uso o mais demorado para paralelismo)
                Menu m = menuDAO.findById(lp.getItemId());
                if (m != null) {
                    tempoItem = m.getLinhas().stream()
                            .mapToDouble(lm -> calcularTempoProduto(lm.getProdutoId()))
                            .max().orElse(0.0);
                }
            }

            // Num sistema paralelo, esperamos pelo item mais demorado
            if (tempoItem > maxMinutos) {
                maxMinutos = tempoItem;
            }
        }
        
        // Adiciona uma margem de segurança (ex: 2 min para embalamento/entrega)
        return maxMinutos > 0 ? maxMinutos + 2.0 : 0.0;
    }

    private double calcularTempoProduto(int produtoId) {
        Produto prod = produtoDAO.findById(produtoId);
        if (prod == null) return 0.0;

        // Soma a duração de todos os passos deste produto
        return prod.getPassoIds().stream()
                .map(passoDAO::findById)
                .filter(Objects::nonNull)
                .mapToDouble(passo -> {
                    if (passo.getDuracao() != null) {
                        return passo.getDuracao().toSeconds() / 60.0; // Converte para minutos
                    }
                    return 0.0;
                })
                .sum();
    }

    // --- Métodos Auxiliares de Lógica de Negócio (Privados) ---

    private boolean contemAlergenicos(Produto p, List<String> alergenicosEvitar) {
        if (alergenicosEvitar == null || alergenicosEvitar.isEmpty()) return false;
        
        // Verifica se algum ingrediente do produto tem alergénios proibidos
        // Assumindo que Produto tem lista de Ingredientes ou getAlergenicos direto
        // Implementação genérica:
        List<Ingrediente> ingredientes = p.getLinhas().stream()
                .map(lp -> obterIngrediente(lp.getIngredienteId()))
                .collect(Collectors.toList());

        for (Ingrediente ing : ingredientes) {
            // Supondo que Ingrediente tem getAlergenico() que devolve String
            if (ing != null && alergenicosEvitar.contains(ing.getAlergenico())) {
                return true;
            }
        }
        return false;
    }

    private boolean verificarStockProduto(Produto p, Restaurante r) {
        List<LinhaProduto> componentes = p.getLinhas();
        
        for (LinhaProduto comp : componentes) {
            Integer qtdNecessaria = comp.getQuantidade();
            Integer ingredienteId = comp.getIngredienteId();

            // Verificar no stock do restaurante
            Integer stockDisponivel = r.getStock().stream()
                    .filter(s -> s.getIngredienteId() == ingredienteId)
                    .map(LinhaStock::getQuantidade)
                    .findFirst()
                    .orElse(0);

            if (stockDisponivel < qtdNecessaria) {
                return false; 
            }
        }
        return true;
    }
}
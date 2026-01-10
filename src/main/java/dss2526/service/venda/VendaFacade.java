package dss2526.service.venda;

import dss2526.data.contract.*;
import dss2526.data.impl.*;
import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;
import dss2526.domain.contract.Item;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Facade para o módulo de Venda/Ponto de Venda.
 * Singleton que oferece operações de venda, gestão de pedidos e processamento de pagamentos.
 *
 * Esta classe implementa APENAS os métodos necessários conforme o Interface Segregation Principle.
 */
public class VendaFacade implements IVendaFacade {
    private static VendaFacade instance;

    // ============ DAOs NECESSÁRIOS ============
    protected final IngredienteDAO ingredienteDAO = IngredienteDAOImpl.getInstance();
    protected final RestauranteDAO restauranteDAO = RestauranteDAOImpl.getInstance();
    protected final CatalogoDAO catalogoDAO = CatalogoDAOImpl.getInstance();
    protected final ProdutoDAO produtoDAO = ProdutoDAOImpl.getInstance();
    protected final MenuDAO menuDAO = MenuDAOImpl.getInstance();
    protected final PassoDAO passoDAO = PassoDAOImpl.getInstance();
    protected final PedidoDAO pedidoDAO = PedidoDAOImpl.getInstance();
    protected final PagamentoDAO pagamentoDAO = PagamentoDAOImpl.getInstance();

    /**
     * Construtor privado (padrão Singleton).
     */
    private VendaFacade() {}

    /**
     * Obtém a instância única (thread-safe).
     */
    public static synchronized VendaFacade getInstance() {
        if (instance == null) {
            instance = new VendaFacade();
        }
        return instance;
    }

    // ============ IMPLEMENTAÇÃO DOS MÉTODOS ============

    @Override
    public List<Ingrediente> listarAlergenicosDisponiveis() {
        return ingredienteDAO.findAll().stream()
                .filter(i -> i.getAlergenico() != null && !i.getAlergenico().isBlank())
                .collect(Collectors.toList());
    }

    @Override
    public List<Restaurante> listarRestaurantes() {
        return restauranteDAO.findAll();
    }

    @Override
    public Pedido iniciarPedido(int restauranteId) {
        Pedido p = new Pedido();
        p.setRestauranteId(restauranteId);
        p.setEstado(EstadoPedido.INICIADO);
        p.setDataCriacao(LocalDateTime.now());
        return pedidoDAO.create(p);
    }

    @Override
    public Pedido obterPedido(int pedidoId) {
        return pedidoDAO.findById(pedidoId);
    }

    @Override
    public List<Item> listarCatalogoFiltrado(int rId, List<Integer> excIds) {
        Restaurante r = restauranteDAO.findById(rId);
        if (r == null) {
            return Collections.emptyList();
        }

        Integer catalogoId = r.getCatalogoId();
        if (catalogoId == null) {
            return Collections.emptyList();
        }

        Catalogo c = catalogoDAO.findById(catalogoId);
        if (c == null) {
            return Collections.emptyList();
        }

        // Lista de exclusão segura (nunca null)
        List<Integer> exclusoes = excIds != null ? excIds : Collections.emptyList();
        List<Item> resultado = new ArrayList<>();

        // 1. Filtrar Produtos
        for (int id : c.getProdutoIds()) {
            Produto p = produtoDAO.findById(id);
            if (p == null) continue;

            List<Integer> idsIngredientes = obterIdsIngredientesDeProduto(id);
            boolean temAlergenio = idsIngredientes.stream().anyMatch(exclusoes::contains);
            boolean temStock = verificarStockPelosIngredientes(r, idsIngredientes);

            if (!temAlergenio && temStock) {
                resultado.add(p);
            }
        }

        // 2. Filtrar Menus
        for (int id : c.getMenuIds()) {
            Menu m = menuDAO.findById(id);
            if (m == null) continue;

            boolean menuInvalido = m.getLinhas().stream().anyMatch(lm -> {
                int prodId = lm.getProdutoId();
                List<Integer> idsIng = obterIdsIngredientesDeProduto(prodId);
                return idsIng.stream().anyMatch(exclusoes::contains) || !verificarStockPelosIngredientes(r, idsIng);
            });

            if (!menuInvalido) {
                resultado.add(m);
            }
        }

        return resultado;
    }

    /**
     * Auxiliar privado: Obtém IDs de ingredientes de um produto (via passos e receita).
     */
    private List<Integer> obterIdsIngredientesDeProduto(int produtoId) {
        Set<Integer> ingIds = new HashSet<>(); // Usar Set para evitar duplicados
        Produto p = produtoDAO.findById(produtoId);

        if (p != null) {
            // Ingredientes dos passos de confeção
            for (int passoId : p.getPassoIds()) {
                Passo passo = passoDAO.findById(passoId);
                if (passo != null) {
                    ingIds.addAll(passo.getIngredienteIds());
                }
            }
            // Ingredientes diretos da receita
            for (LinhaProduto lp : p.getLinhas()) {
                ingIds.add(lp.getIngredienteId());
            }
        }

        return new ArrayList<>(ingIds);
    }

    /**
     * Auxiliar privado: Verifica se há stock para todos os ingredientes.
     */
    private boolean verificarStockPelosIngredientes(Restaurante r, List<Integer> ingIdsNecessarios) {
        if (ingIdsNecessarios.isEmpty()) {
            return true; // Produto sem ingredientes está sempre disponível
        }

        for (int iId : ingIdsNecessarios) {
            boolean disponivel = r.getStock().stream()
                    .anyMatch(ls -> ls.getIngredienteId() == iId && ls.getQuantidade() > 0);
            if (!disponivel) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void adicionarItemAoPedido(int pId, int iId, TipoItem t, int q, String o) {
        Pedido p = pedidoDAO.findById(pId);
        if (p == null) {
            return;
        }

        // CORRIGIDO: Validar que o item existe antes de adicionar
        double preco = 0.0;
        if (t == TipoItem.PRODUTO) {
            Produto produto = produtoDAO.findById(iId);
            if (produto == null) {
                return; // Produto não existe
            }
            preco = produto.getPreco();
        } else {
            Menu menu = menuDAO.findById(iId);
            if (menu == null) {
                return; // Menu não existe
            }
            preco = menu.getPreco();
        }

        LinhaPedido lp = new LinhaPedido();
        lp.setPedidoId(pId);
        lp.setItemId(iId);
        lp.setTipo(t);
        lp.setQuantidade(q);
        lp.setObservacao(o != null ? o : "");
        lp.setPrecoUnitario(preco);

        p.addLinha(lp);
        pedidoDAO.update(p);
    }

    @Override
    public void removerQuantidadeDoPedido(int pId, int lId, int qtd) {
        Pedido p = pedidoDAO.findById(pId);
        if (p == null) {
            return;
        }

        p.getLinhas().stream()
                .filter(l -> l.getId() == lId)
                .findFirst()
                .ifPresent(lp -> {
                    if (lp.getQuantidade() <= qtd) {
                        p.getLinhas().remove(lp);
                    } else {
                        lp.setQuantidade(lp.getQuantidade() - qtd);
                    }
                    pedidoDAO.update(p);
                });
    }

    @Override
    public void cancelarPedido(int pId) {
        Pedido p = pedidoDAO.findById(pId);
        if (p != null && p.getEstado() == EstadoPedido.INICIADO) {
            p.setEstado(EstadoPedido.CANCELADO);
            pedidoDAO.update(p);
        }
    }

    @Override
    public Duration processarPagamento(int pId, TipoPagamento tipo) {
        Pedido p = pedidoDAO.findById(pId);
        if (p == null) {
            return Duration.ZERO;
        }

        // Verificar se o pedido está no estado correto
        if (p.getEstado() != EstadoPedido.INICIADO) {
            return Duration.ZERO;
        }

        // Criar o registo de pagamento
        Pagamento pag = new Pagamento();
        pag.setPedidoId(pId);
        pag.setTipo(tipo);
        pag.setValor(p.calcularPrecoTotal());
        pag.setData(LocalDateTime.now());

        Duration estimativa = Duration.ZERO;

        if (tipo == TipoPagamento.TERMINAL) {
            // Pagamento imediato no terminal
            pag.setConfirmado(true);
            p.setEstado(EstadoPedido.CONFIRMADO);
            estimativa = calcularEstimativaEspera(p);
        } else {
            // Pagamento pendente na caixa
            pag.setConfirmado(false);
            p.setEstado(EstadoPedido.AGUARDA_PAGAMENTO);
        }

        pagamentoDAO.create(pag);
        pedidoDAO.update(p);

        return estimativa;
    }

    /**
     * Auxiliar privado: Calcula estimativa de tempo de espera baseada nos passos mais longos.
     */
    private Duration calcularEstimativaEspera(Pedido p) {
        long maxMinutos = 0;

        for (LinhaPedido lp : p.getLinhas()) {
            if (lp.getTipo() == TipoItem.PRODUTO) {
                maxMinutos = Math.max(maxMinutos, obterDuracaoMaximaProduto(lp.getItemId()));
            } else {
                Menu m = menuDAO.findById(lp.getItemId());
                if (m != null) {
                    for (LinhaMenu lm : m.getLinhas()) {
                        maxMinutos = Math.max(maxMinutos, obterDuracaoMaximaProduto(lm.getProdutoId()));
                    }
                }
            }
        }

        // Adicionar margem de 5 minutos para preparação geral
        return Duration.ofMinutes(maxMinutos + 5);
    }

    /**
     * Auxiliar privado: Obtém duração máxima de todos os passos de um produto.
     */
    private long obterDuracaoMaximaProduto(int prodId) {
        Produto pr = produtoDAO.findById(prodId);
        if (pr == null || pr.getPassoIds().isEmpty()) {
            return 0;
        }

        return pr.getPassoIds().stream()
                .map(passoDAO::findById)
                .filter(Objects::nonNull)
                .mapToLong(passo -> passo.getDuracao().toMinutes())
                .max()
                .orElse(0);
    }

    @Override
    public List<Pedido> listarPedidosAtivos(int rId) {
        return pedidoDAO.findAllByRestaurante(rId).stream()
                .filter(p -> p.getEstado() == EstadoPedido.CONFIRMADO ||
                        p.getEstado() == EstadoPedido.EM_PREPARACAO ||
                        p.getEstado() == EstadoPedido.PRONTO)
                .collect(Collectors.toList());
    }

    @Override
    public String obterNomeItem(int itemId, TipoItem tipo) {
        if (tipo == TipoItem.PRODUTO) {
            Produto p = produtoDAO.findById(itemId);
            return p != null ? p.getNome() : "Produto Desconhecido";
        }
        Menu m = menuDAO.findById(itemId);
        return m != null ? m.getNome() : "Menu Desconhecido";
    }
}
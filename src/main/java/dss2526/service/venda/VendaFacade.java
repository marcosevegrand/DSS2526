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
 * Esta classe implementa APENAS os métodos necessários, não herdando de BaseFacade.
 */
public class VendaFacade implements IVendaFacade {
    private static VendaFacade instance;
    
    // ============ DAOs NECESSÁRIOS (7 de 12) ============
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
        if (r == null) return Collections.emptyList();
        
        Catalogo c = catalogoDAO.findById(r.getCatalogoId());
        if (c == null) return Collections.emptyList();
        
        List<Item> res = new ArrayList<>();
        
        // 1. Filtrar Produtos
        for (int id : c.getProdutoIds()) {
            Produto p = produtoDAO.findById(id);
            if (p == null) continue;
            List<Integer> idsIngredientes = obterIdsIngredientesDeProduto(id);
            boolean temAlergenio = idsIngredientes.stream().anyMatch(excIds::contains);
            boolean temStock = verificarStockPelosIngredientes(r, idsIngredientes);
            if (!temAlergenio && temStock) res.add(p);
        }
        
        // 2. Filtrar Menus
        for (int id : c.getMenuIds()) {
            Menu m = menuDAO.findById(id);
            if (m == null) continue;
            boolean menuInvalido = m.getLinhas().stream().anyMatch(lm -> {
                int prodId = lm.getProdutoId();
                List<Integer> idsIng = obterIdsIngredientesDeProduto(prodId);
                return idsIng.stream().anyMatch(excIds::contains) || !verificarStockPelosIngredientes(r, idsIng);
            });
            if (!menuInvalido) res.add(m);
        }
        return res;
    }

    /**
     * Auxiliar privado: Obtém IDs de ingredientes de um produto.
     */
    private List<Integer> obterIdsIngredientesDeProduto(int produtoId) {
        List<Integer> ingIds = new ArrayList<>();
        Produto p = produtoDAO.findById(produtoId);
        if (p != null) {
            for (int passoId : p.getPassoIds()) {
                Passo passo = passoDAO.findById(passoId);
                if (passo != null) ingIds.addAll(passo.getIngredienteIds());
            }
            p.getLinhas().forEach(l -> ingIds.add(l.getIngredienteId()));
        }
        return ingIds;
    }

    /**
     * Auxiliar privado: Verifica se há stock para todos os ingredientes.
     */
    private boolean verificarStockPelosIngredientes(Restaurante r, List<Integer> ingIdsNecessarios) {
        for (int iId : ingIdsNecessarios) {
            boolean disponivel = r.getStock().stream()
                    .anyMatch(ls -> ls.getIngredienteId() == iId && ls.getQuantidade() > 0);
            if (!disponivel) return false;
        }
        return true;
    }

    @Override
    public void adicionarItemAoPedido(int pId, int iId, TipoItem t, int q, String o) {
        Pedido p = pedidoDAO.findById(pId);
        if (p == null) return;
        LinhaPedido lp = new LinhaPedido();
        lp.setPedidoId(pId);
        lp.setItemId(iId);
        lp.setTipo(t);
        lp.setQuantidade(q);
        lp.setObservacao(o);
        double preco = (t == TipoItem.PRODUTO) ? 
            produtoDAO.findById(iId).getPreco() : 
            menuDAO.findById(iId).getPreco();
        lp.setPrecoUnitario(preco);
        p.addLinha(lp);
        pedidoDAO.update(p);
    }

    @Override
    public void removerQuantidadeDoPedido(int pId, int lId, int qtd) {
        Pedido p = pedidoDAO.findById(pId);
        if (p == null) return;
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
        if (p == null) return null;
        
        Pagamento pag = new Pagamento();
        pag.setPedidoId(pId);
        pag.setTipo(tipo);
        pag.setValor(p.calcularPrecoTotal());
        pag.setData(LocalDateTime.now());
        
        Duration estimativa = Duration.ZERO;

        if (tipo == TipoPagamento.TERMINAL) {
            pag.setConfirmado(true);
            p.setEstado(EstadoPedido.CONFIRMADO);
            // Calcular estimativa apenas se pago
            estimativa = calcularEstimativaEspera(p);
        } else {
            pag.setConfirmado(false);
            p.setEstado(EstadoPedido.AGUARDA_PAGAMENTO);
            // Se for na caixa, a estimativa só é dada lá
        }
        
        pagamentoDAO.create(pag);
        pedidoDAO.update(p);
        
        return estimativa;
    }

    /**
     * Auxiliar privado: Calcula estimação de espera.
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
        return Duration.ofMinutes(maxMinutos + 5);
    }

    /**
     * Auxiliar privado: Obtém duração máxima de um produto.
     */
    private long obterDuracaoMaximaProduto(int prodId) {
        Produto pr = produtoDAO.findById(prodId);
        if (pr == null) return 0;
        return pr.getPassoIds().stream()
                .map(passoDAO::findById)
                .filter(Objects::nonNull)
                .mapToLong(passo -> passo.getDuracao().toMinutes())
                .max().orElse(0);
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
            return p != null ? p.getNome() : "Desconhecido";
        }
        Menu m = menuDAO.findById(itemId);
        return m != null ? m.getNome() : "Desconhecido";
    }
}

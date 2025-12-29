package dss2526.service.venda;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;
import dss2526.domain.contract.Item;
import dss2526.service.base.BaseFacade;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class VendaFacade extends BaseFacade implements IVendaFacade {
    private static VendaFacade instance;
    private VendaFacade() {}
    public static synchronized VendaFacade getInstance() {
        if (instance == null) instance = new VendaFacade();
        return instance;
    }

    @Override
    public List<Ingrediente> listarAlergenicosDisponiveis() {
        return ingredienteDAO.findAll().stream()
                .filter(i -> i.getAlergenico() != null && !i.getAlergenico().isBlank())
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> listarCatalogoFiltrado(int rId, List<Integer> excIds) {
        Restaurante r = restauranteDAO.findById(rId);
        Catalogo c = catalogoDAO.findById(r.getCatalogoId());
        List<Item> res = new ArrayList<>();
        
        // 1. Filtrar Produtos
        for (int id : c.getProdutoIds()) {
            Produto p = produtoDAO.findById(id);
            if (p == null) continue;

            // CRÍTICO: Obter ingredientes através dos PASSOS (onde os dados estão no SQL)
            List<Integer> idsIngredientes = obterIdsIngredientesDeProduto(id);
            
            // Verificar Alergénios
            boolean temAlergenio = idsIngredientes.stream().anyMatch(excIds::contains);
            
            // Verificar Stock (Quantidade > 0 dos ingredientes necessários)
            boolean temStock = verificarStockPelosIngredientes(r, idsIngredientes);
            
            if (!temAlergenio && temStock) res.add(p);
        }
        
        // 2. Filtrar Menus
        for (int id : c.getMenuIds()) {
            Menu m = menuDAO.findById(id);
            if (m == null) continue;

            // Menu é inválido se algum dos seus produtos for inválido
            boolean menuInvalido = m.getLinhas().stream().anyMatch(lm -> {
                int prodId = lm.getProdutoId();
                List<Integer> idsIng = obterIdsIngredientesDeProduto(prodId);
                
                boolean pTemAlergenio = idsIng.stream().anyMatch(excIds::contains);
                boolean pSemStock = !verificarStockPelosIngredientes(r, idsIng);
                
                return pTemAlergenio || pSemStock;
            });
            
            if (!menuInvalido) res.add(m);
        }
        return res;
    }

    /**
     * Resolve a hierarquia Produto -> Passo -> Ingrediente utilizando as Entidades.
     */
    private List<Integer> obterIdsIngredientesDeProduto(int produtoId) {
        List<Integer> ingIds = new ArrayList<>();
        Produto p = produtoDAO.findById(produtoId);
        if (p != null) {
            // Percorre os Passos definidos no Produto
            for (int passoId : p.getPassoIds()) {
                Passo passo = passoDAO.findById(passoId);
                if (passo != null) {
                    // Adiciona os ingredientes do Passo
                    ingIds.addAll(passo.getIngredienteIds());
                }
            }
            // Adiciona também ingredientes diretos (LinhaProduto) se existirem
            p.getLinhas().forEach(l -> ingIds.add(l.getIngredienteId()));
        }
        return ingIds;
    }

    private boolean verificarStockPelosIngredientes(Restaurante r, List<Integer> ingIdsNecessarios) {
        // Verifica se o restaurante tem stock > 0 para todos os ingredientes da lista
        for (int iId : ingIdsNecessarios) {
            boolean disponivel = r.getStock().stream()
                    .anyMatch(ls -> ls.getIngredienteId() == iId && ls.getQuantidade() > 0);
            if (!disponivel) return false;
        }
        return true;
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
    public void adicionarItemAoPedido(int pId, int iId, TipoItem t, int q, String o) {
        Pedido p = pedidoDAO.findById(pId);
        if (p == null) return;
        LinhaPedido lp = new LinhaPedido();
        lp.setPedidoId(pId); lp.setItemId(iId); lp.setTipo(t); lp.setQuantidade(q); lp.setObservacao(o);
        double preco = (t == TipoItem.PRODUTO) ? produtoDAO.findById(iId).getPreco() : menuDAO.findById(iId).getPreco();
        lp.setPrecoUnitario(preco);
        p.addLinha(lp);
        pedidoDAO.update(p);
    }

    @Override
    public void removerQuantidadeDoPedido(int pId, int lId, int qtd) {
        Pedido p = pedidoDAO.findById(pId);
        if (p == null) return;
        p.getLinhas().stream().filter(l -> l.getId() == lId).findFirst().ifPresent(lp -> {
            if (lp.getQuantidade() <= qtd) p.getLinhas().remove(lp);
            else lp.setQuantidade(lp.getQuantidade() - qtd);
            pedidoDAO.update(p);
        });
    }

    @Override
    public Pagamento processarPagamento(int pId, TipoPagamento tipo) {
        Pedido p = pedidoDAO.findById(pId);
        Pagamento pag = new Pagamento();
        pag.setPedidoId(pId); pag.setTipo(tipo); pag.setValor(p.calcularPrecoTotal()); pag.setData(LocalDateTime.now());
        if (tipo == TipoPagamento.TERMINAL) { 
            pag.setConfirmado(true); 
            p.setEstado(EstadoPedido.CONFIRMADO); 
        } else { 
            pag.setConfirmado(false); 
            p.setEstado(EstadoPedido.AGUARDA_PAGAMENTO); 
        }
        pagamentoDAO.create(pag);
        pedidoDAO.update(p);
        return pag;
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

    @Override public List<Restaurante> listarRestaurantes() { return restauranteDAO.findAll(); }
    @Override public Pedido obterPedido(int pId) { return pedidoDAO.findById(pId); }
    @Override public void cancelarPedido(int pId) { 
        Pedido p = pedidoDAO.findById(pId);
        if(p != null) { p.setEstado(EstadoPedido.CANCELADO); pedidoDAO.update(p); }
    }
}
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
    public Pedido iniciarPedido(int restauranteId) {
        Pedido p = new Pedido();
        p.setRestauranteId(restauranteId);
        p.setEstado(EstadoPedido.INICIADO);
        p.setDataCriacao(LocalDateTime.now());
        return pedidoDAO.create(p);
    }

    @Override
    public List<Ingrediente> listarAlergenicosDisponiveis() {
        return ingredienteDAO.findAll().stream()
                .filter(i -> i.getAlergenico() != null && !i.getAlergenico().isBlank())
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> listarCatalogoFiltrado(int rId, List<Integer> exc) {
        Restaurante r = restauranteDAO.findById(rId);
        Catalogo c = catalogoDAO.findById(r.getCatalogoId());
        List<Item> res = new ArrayList<>();
        
        for (int id : c.getProdutoIds()) {
            Produto p = produtoDAO.findById(id);
            if (p.getLinhas().stream().noneMatch(l -> exc.contains(l.getIngredienteId()))) res.add(p);
        }
        for (int id : c.getMenuIds()) {
            Menu m = menuDAO.findById(id);
            boolean p = m.getLinhas().stream().anyMatch(lm -> {
                Produto pr = produtoDAO.findById(lm.getProdutoId());
                return pr.getLinhas().stream().anyMatch(l -> exc.contains(l.getIngredienteId()));
            });
            if (!p) res.add(m);
        }
        return res;
    }

    @Override
    public void adicionarItemAoPedido(int pId, int iId, TipoItem t, int q, String o) {
        Pedido p = pedidoDAO.findById(pId);
        LinhaPedido lp = new LinhaPedido();
        lp.setPedidoId(pId); lp.setItemId(iId); lp.setTipo(t); lp.setQuantidade(q); lp.setObservacao(o);
        lp.setPrecoUnitario(t == TipoItem.PRODUTO ? produtoDAO.findById(iId).getPreco() : menuDAO.findById(iId).getPreco());
        p.addLinha(lp);
        pedidoDAO.update(p);
    }

    @Override
    public void removerItemDoPedido(int pId, int lId) {
        Pedido p = pedidoDAO.findById(pId);
        p.getLinhas().removeIf(l -> l.getId() == lId);
        pedidoDAO.update(p);
    }

    @Override
    public void cancelarPedido(int pId) {
        Pedido p = pedidoDAO.findById(pId);
        if (p != null) { p.setEstado(EstadoPedido.CANCELADO); pedidoDAO.update(p); }
    }

    @Override
    public Pagamento processarPagamento(int pId, TipoPagamento tipo) {
        Pedido p = pedidoDAO.findById(pId);
        Pagamento pag = new Pagamento();
        pag.setPedidoId(pId);
        pag.setTipo(tipo);
        pag.setValor(p.calcularPrecoTotal());
        
        if (tipo == TipoPagamento.TERMINAL) {
            pag.setConfirmado(true);
            pag.setData(LocalDateTime.now());
            p.setEstado(EstadoPedido.CONFIRMADO); // Pagamento automático confirma pedido
        } else {
            pag.setConfirmado(false);
            p.setEstado(EstadoPedido.AGUARDA_PAGAMENTO); // Precisa de intervenção na Caixa
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
}
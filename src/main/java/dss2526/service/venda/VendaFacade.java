package dss2526.service.venda;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;
import dss2526.domain.contract.Item;
import dss2526.service.base.BaseFacade;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
                .collect(Collectors.toMap(Ingrediente::getAlergenico, i -> i, (i1, i2) -> i1))
                .values().stream().collect(Collectors.toList());
    }

    @Override
    public List<Item> listarItemsDisponiveis(int restauranteId, List<Integer> alergenicoIds) {
        Restaurante r = restauranteDAO.findById(restauranteId);
        if (r == null) return new ArrayList<>();
        Catalogo c = catalogoDAO.findById(r.getCatalogoId());
        List<Item> res = new ArrayList<>();
        if (c == null) return res;

        for (int pid : c.getProdutoIds()) {
            Produto p = produtoDAO.findById(pid);
            if (p != null && !contemAlergenicos(p, alergenicoIds)) res.add(p);
        }
        for (int mid : c.getMenuIds()) {
            Menu m = menuDAO.findById(mid);
            if (m != null && m.getLinhas().stream().allMatch(lm -> {
                Produto p = produtoDAO.findById(lm.getProdutoId());
                return p != null && !contemAlergenicos(p, alergenicoIds);
            })) res.add(m);
        }
        return res;
    }

    @Override
    public boolean adicionarLinhaAoPedido(int pedidoId, int itemId, TipoItem tipo, int quantidade, String obs) {
        Pedido p = pedidoDAO.findById(pedidoId);
        if (p == null) return false;
        LinhaPedido lp = new LinhaPedido();
        lp.setPedidoId(pedidoId);
        lp.setItemId(itemId);
        lp.setTipo(tipo);
        lp.setQuantidade(quantidade);
        lp.setObservacao(obs);
        double preco = (tipo == TipoItem.PRODUTO) ? 
                produtoDAO.findById(itemId).getPreco() : menuDAO.findById(itemId).getPreco();
        lp.setPrecoUnitario(preco);
        p.addLinha(lp);
        pedidoDAO.update(p);
        return true;
    }

    @Override
    public boolean removerLinhaDoPedido(int pedidoId, int linhaPedidoId) {
        Pedido p = pedidoDAO.findById(pedidoId);
        if (p == null) return false;
        boolean removido = p.getLinhas().removeIf(l -> l.getId() == linhaPedidoId);
        if (removido) {
            pedidoDAO.update(p);
        }
        return removido;
    }

    @Override
    public void cancelarPedidoVenda(int pedidoId) {
        Pedido p = pedidoDAO.findById(pedidoId);
        if (p != null) {
            p.setEstado(EstadoPedido.CANCELADO);
            pedidoDAO.update(p);
        }
    }

    @Override
    public double obterEstimativaEntrega(int pedidoId) {
        return 15.0; 
    }

    @Override
    public List<Pedido> listarPedidosAtivos(int restauranteId) {
        return pedidoDAO.findAllByRestaurante(restauranteId).stream()
                .filter(p -> p.getEstado() != EstadoPedido.ENTREGUE && p.getEstado() != EstadoPedido.CANCELADO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TipoPagamento> listarOpcoesPagamento(int restauranteId) {
        return List.of(TipoPagamento.TERMINAL, TipoPagamento.CAIXA);
    }

    @Override
    public Pagamento criarPagamento(int pedidoId, TipoPagamento tipo) {
        Pedido p = pedidoDAO.findById(pedidoId);
        Pagamento pag = new Pagamento();
        pag.setPedidoId(pedidoId);
        pag.setValor(p.calcularPrecoTotal());
        pag.setTipo(tipo);
        pag.setConfirmado(false);
        pag.setData(LocalDateTime.now());
        return pagamentoDAO.create(pag);
    }

    @Override
    public boolean confirmarPagamento(int pagamentoId) {
        Pagamento pag = pagamentoDAO.findById(pagamentoId);
        if (pag == null) return false;
        pag.setConfirmado(true);
        pag.setData(LocalDateTime.now());
        pagamentoDAO.update(pag);
        return true;
    }

    @Override
    public boolean confirmarPedido(int pedidoId) {
        Pedido p = pedidoDAO.findById(pedidoId);
        if (p == null) return false;
        
        Pagamento pag = pagamentoDAO.findByPedido(pedidoId);
        if (pag != null && pag.isConfirmado()) {
            p.setEstado(EstadoPedido.CONFIRMADO);
        } else {
            p.setEstado(EstadoPedido.AGUARDA_PAGAMENTO);
        }
        
        pedidoDAO.update(p);
        return true;
    }

    private boolean contemAlergenicos(Produto p, List<Integer> alergenicoIds) {
        if (alergenicoIds == null || alergenicoIds.isEmpty()) return false;
        return p.getLinhas().stream().anyMatch(l -> alergenicoIds.contains(l.getIngredienteId()));
    }
}
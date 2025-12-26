package dss2526.service.venda;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;
import dss2526.service.base.BaseFacade;

import java.util.List;
import java.time.LocalDateTime;

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
        return registarPedido(p);
    }

    @Override
    public List<Produto> listarProdutosDisponiveis(Restaurante restaurante) {
        Catalogo catalogo = obterCatalogo(restaurante.getCatalogoId());
        List<Integer> produtoIds = catalogo.getProdutoIds();
        
        return produtoIds.stream()
                .map(this::obterProduto)
                .filter(produto -> verificarIngredientesDisponiveis(produto, restaurante))
                .toList();
    }

    public Boolean verificarIngredientesDisponiveis(Produto p, Restaurante r) {
        List<LinhaProduto> linhaProdutos = p.getLinhas();
        
        for (LinhaProduto lp : linhaProdutos) {
            Integer quantidadeNecessaria = lp.getQuantidade();
            Integer quantidadeDisponivel = r.getStock().stream().
                    filter(s -> s.getIngredienteId() == lp.getIngredienteId())
                    .map(LinhaStock::getQuantidade)
                    .findFirst()
                    .orElse(0);
            
            if (quantidadeDisponivel < quantidadeNecessaria) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Menu> listarMenusDisponiveis(Restaurante restaurante) {
        Catalogo catalogo = obterCatalogo(restaurante.getCatalogoId());
        List<Integer> menuIds = catalogo.getMenuIds();
        
        return menuIds.stream()
                .map(this::obterMenu)
                .filter(menu -> menu.getLinhas().stream()
                        .map(lm -> obterProduto(lm.getProdutoId()))
                        .allMatch(produto -> verificarIngredientesDisponiveis(produto, restaurante)))
                .toList();
    }
}
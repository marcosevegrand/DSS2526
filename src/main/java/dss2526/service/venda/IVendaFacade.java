package dss2526.service.venda;

import dss2526.domain.entity.*;
import dss2526.service.base.IBaseFacade;

import java.util.List;

public interface IVendaFacade extends IBaseFacade {

    Pedido iniciarPedido(Restaurante restaurante, Boolean paraLevar);

    List<Produto> listarProdutosDisponiveis(Restaurante restaurante);
    List<Menu> listarMenusDisponiveis(Restaurante restaurante);
}
package pt.uminho.dss.restaurante.venda;

import pt.uminho.dss.restaurante.core.domain.entity.Menu;
import pt.uminho.dss.restaurante.core.domain.entity.Pedido;
import pt.uminho.dss.restaurante.core.domain.entity.Produto;
import pt.uminho.dss.restaurante.core.domain.entity.Talao;
import pt.uminho.dss.restaurante.core.domain.enumeration.*;
import pt.uminho.dss.restaurante.persistence.contract.MenuDAO;
import pt.uminho.dss.restaurante.persistence.contract.PedidoDAO;
import pt.uminho.dss.restaurante.persistence.contract.ProdutoDAO;

public class VendaFacade implements IVenda {

    public VendaFacade(
        PedidoDAO pedidoDAO,
        ProdutoDAO produtoDAO,
        MenuDAO menuDAO,
        TalaoDAO talaoDAO
    ) {
        this.pedidoDAO = pedidoDAO;
        this.produtoDAO = produtoDAO;
        this.menuDAO = menuDAO;
        this.talaoDAO = talaoDAO;
    }
}

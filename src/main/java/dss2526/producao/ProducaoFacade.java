package dss2526.producao;

import dss2526.data.contract.PedidoDAO;
import dss2526.data.contract.TarefaDAO;

public class ProducaoFacade implements IProducaoFacade {

    private final TarefaDAO tarefaDAO;
    private final PedidoDAO pedidoDAO;

    public ProducaoFacade(TarefaDAO tarefaDAO, PedidoDAO pedidoDAO) {
        this.tarefaDAO = tarefaDAO;
        this.pedidoDAO = pedidoDAO;
    }
}
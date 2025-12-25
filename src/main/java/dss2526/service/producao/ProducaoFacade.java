package dss2526.service.producao;

import dss2526.data.impl.*;
import dss2526.data.contract.*;
import dss2526.domain.entity.*;
import java.util.List;

public class ProducaoFacade implements IProducaoFacade {

    private static ProducaoFacade instance;

    private final PedidoDAO pedidoDAO;
    private final TarefaDAO tarefaDAO;
    private final EstacaoDAO estacaoDAO;

    private ProducaoFacade() {
        this.pedidoDAO = PedidoDAOImpl.getInstance();
        this.tarefaDAO = TarefaDAOImpl.getInstance();
        this.estacaoDAO = EstacaoDAOImpl.getInstance();
    }

    public static synchronized ProducaoFacade getInstance() {
        if (instance == null) {
            instance = new ProducaoFacade();
        }
        return instance;
    }

    @Override
    public List<Pedido> consultarFilaPedidos(int restauranteId) {
        // Logic to filter orders that are not yet "ENTREGUE"
        // This relies on the DAO fetching by restaurant
        return pedidoDAO.findAllByRestaurante(restauranteId);
    }

    @Override
    public void atualizarEstadoTarefa(int tarefaId, boolean concluida) {
        Tarefa t = tarefaDAO.findById(tarefaId);
        if (t != null) {
            t.setConcluido(concluida);
            tarefaDAO.update(t);
        }
    }

    @Override
    public void atribuirTarefaEstacao(int tarefaId, int estacaoId) {
        // Business logic to assign logic would go here
    }
    
    @Override
    public List<Estacao> getEstacoes(int restauranteId) {
        return estacaoDAO.findAllByRestaurante(restauranteId);
    }
}
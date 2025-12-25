package dss2526.ui.controller;

import dss2526.domain.entity.Pedido;
import dss2526.service.producao.IProducaoFacade;
import dss2526.service.producao.ProducaoFacade;
import java.util.List;

public class ProducaoController {

    private IProducaoFacade producaoFacade;

    public ProducaoController() {
        // Connect to the Singleton Facade
        this.producaoFacade = ProducaoFacade.getInstance();
    }

    public List<Pedido> getFilaPedidos(int restauranteId) {
        return producaoFacade.consultarFilaPedidos(restauranteId);
    }

    public void concluirTarefa(int tarefaId) {
        producaoFacade.atualizarEstadoTarefa(tarefaId, true);
    }
}
package dss2526.ui.controller;

import java.util.List;

import dss2526.domain.entity.Restaurante;
import dss2526.service.venda.*;

public class VendaController {

    private IVendaFacade vendaFacade;

    public VendaController() {
        this.vendaFacade = VendaFacade.getInstance();
    }

    public void novoPedido() {}

    public List<String> listarRestaurantes() {
        List<Restaurante> restaurantes = vendaFacade.listarRestaurantes();
        return restaurantes.stream()
                .map(r -> String.format("ID: %d, Nome: %s, Localização: %s",
                        r.getId(), r.getNome(), r.getLocalizacao()))
                .toList();
    }
}
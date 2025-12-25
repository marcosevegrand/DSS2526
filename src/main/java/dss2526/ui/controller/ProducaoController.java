package dss2526.ui.controller;

import java.util.List;

import dss2526.domain.entity.Restaurante;
import dss2526.service.producao.*;

public class ProducaoController {

    private IProducaoFacade producaoFacade;

    public ProducaoController() {
        this.producaoFacade = ProducaoFacade.getInstance();
    }
}
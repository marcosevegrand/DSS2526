package dss2526.ui.controller;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;
import dss2526.service.gestao.GestaoFacade;
import dss2526.service.gestao.IGestaoFacade;
import java.util.List;

public class GestaoController {

    private IGestaoFacade gestaoFacade;

    public GestaoController() {
        this.gestaoFacade = GestaoFacade.getInstance();
    }

    public void registarRestaurante(String nome, String localizacao) {
        Restaurante r = new Restaurante();
        r.setNome(nome);
        r.setLocalizacao(localizacao);
        gestaoFacade.registarRestaurante(r);
    }

    public List<String> listarRestaurantes() {
        List<Restaurante> restaurantes = gestaoFacade.listarRestaurantes();
        return restaurantes.stream()
                .map(r -> String.format("ID: %d, Nome: %s, Localização: %s",
                        r.getId(), r.getNome(), r.getLocalizacao()))
                .toList();
    }

    // ... Other methods would go here ...
}
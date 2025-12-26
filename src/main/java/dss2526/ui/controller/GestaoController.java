package dss2526.ui.controller;

import dss2526.domain.entity.*;
import dss2526.service.gestao.*;
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
                .map(r -> String.format("Nome: %s, Localização: %s",
                     r.getNome(), r.getLocalizacao()))
                .toList();
    }

    public void removerRestaurante(Integer index) {
        List<Restaurante> restaurantes = gestaoFacade.listarRestaurantes();
        if (index >= 0 && index < restaurantes.size()) {
            Restaurante r = restaurantes.get(index);
            gestaoFacade.removerRestaurante(r.getId());
        }
    }


}
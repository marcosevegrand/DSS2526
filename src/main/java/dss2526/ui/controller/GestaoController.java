package dss2526.ui.controller;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;
import dss2526.service.gestao.GestaoFacade;
import dss2526.service.gestao.IGestaoFacade;
import java.util.List;

public class GestaoController {

    private IGestaoFacade gestaoFacade;

    public GestaoController() {
        // Connect to the Singleton Facade
        this.gestaoFacade = GestaoFacade.getInstance();
    }

    public void adicionarRestaurante(String nome, String localizacao) {
        Restaurante r = new Restaurante();
        r.setNome(nome);
        r.setLocalizacao(localizacao);
        gestaoFacade.registarRestaurante(r);
    }

    public List<String> listarRestaurantes() {
        List<Restaurante> restaurantes = gestaoFacade.getRestaurantes();
        return restaurantes.stream()
                .map(r -> String.format("ID: %d, Nome: %s, Localização: %s",
                        r.getId(), r.getNome(), r.getLocalizacao()))
                .toList();
    }

    public void definirCatalogoRestaurante(Integer restauranteId, Integer catalogoIndex) {
        List<Catalogo> catalogos = gestaoFacade.getCatalogos();
        if (catalogoIndex < 0 || catalogoIndex >= catalogos.size()) {
            throw new IllegalArgumentException("Índice de catálogo inválido.");
        }
        Catalogo catalogo = catalogos.get(catalogoIndex);
        gestaoFacade.definirCatalogoRestaurante(restauranteId, catalogo);
    }

    public List<String> listarCatalogos() {
        List<Catalogo> catalogos = gestaoFacade.getCatalogos();
        return catalogos.stream()
                .map(c -> String.format("ID: %d, Nome: %s",
                        c.getId(), c.getNome()))
                .toList();
    }
    
    public void adicionarEstacao(Integer restauranteId, String trabalho) {
        Estacao e = new Estacao();
        e.setRestauranteId(restauranteId);
        e.setTrabalho(Trabalho.valueOf(trabalho));
        gestaoFacade.registarEstacao(e);
    }

    public List<String> listarEstacoes(Integer restauranteId) {
        return gestaoFacade.getEstacoes().stream()
                .filter(e -> e.getRestauranteId() == restauranteId)
                .map(e -> String.format("ID: %d, Restaurante ID: %d, Trabalho: %s",
                        e.getId(), e.getRestauranteId(), e.getTrabalho().name()))
                .toList();
    }

    public List<String> listarTiposEstacao() {
        return List.of(
            Trabalho.BEBIDAS.name(),
            Trabalho.GELADOS.name(),
            Trabalho.MONTAGEM.name(),
            Trabalho.CAIXA.name(),
            Trabalho.GRELHA.name(),
            Trabalho.FRITURA.name()
        );
    }

    public List<String> listarFuncoes() {
        return List.of(
            Funcao.FUNCIONARIO.name(),
            Funcao.GERENTE.name(),
            Funcao.COO.name(),
            Funcao.SYSADMIN.name()
        );
    }

    public void adicionarFuncionario(Integer restauranteIndex, String utilizador, String pass, Integer funcaoIndex) {
        Funcionario f = new Funcionario();
        f.setRestauranteId(restauranteIndex);
        f.setUtilizador(utilizador);
        f.setPassword(pass);
        f.setFuncao(Funcao.valueOf(listarFuncoes().get(funcaoIndex)));
        gestaoFacade.registarFuncionario(f);
    }

    public void adicionarIngredienteStock(Integer restauranteIndex, String nome, String unidadeMedida, int quantidade, String alergenico) {
        Ingrediente i = new Ingrediente();
        i.setNome(nome);
        i.setUnidade(unidadeMedida);
        i.setAlergenico(alergenico);
        gestaoFacade.adicionarIngredienteStock(restauranteIndex, i);
    }

    public void aumentarIngredienteStock(Integer restauranteIndex, Integer ingredienteIndex, int quantidade) {
        gestaoFacade.aumentarIngredienteStock(restauranteIndex, ingredienteIndex, quantidade);
    }

    public void diminuirIngredienteStock(Integer restauranteIndex, Integer ingredienteIndex, int quantidade) {
        gestaoFacade.diminuirIngredienteStock(restauranteIndex, ingredienteIndex, quantidade);
    }
    public List<String> listarIngredientes(Integer restauranteIndex) {
        return gestaoFacade.getIngredientesStock(restauranteIndex).stream()
                .map(i -> String.format("ID: %d, Nome: %s, Unidade: %s, Alergénico: %s",
                        i.getId(), i.getNome(), i.getUnidade(), i.getAlergenico()))
                .toList();
    }
    
    public List<Menu> getMenus() {
        return gestaoFacade.getMenus();
    }
    
    // Wrapper methods for the UI to call
}
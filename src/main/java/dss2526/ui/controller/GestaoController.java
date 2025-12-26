package dss2526.ui.controller;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.Funcao;
import dss2526.service.gestao.GestaoFacade;
import dss2526.service.gestao.IGestaoFacade;
import java.util.List;

public class GestaoController {
    private final IGestaoFacade facade;
    private Funcionario utilizadorLogado;
    private int restauranteAtivoId = -1;

    public GestaoController() {
        this.facade = GestaoFacade.getInstance();
    }

    public boolean login(String u, String p) {
        this.utilizadorLogado = facade.login(u, p);
        if (this.utilizadorLogado != null) {
            // Se for Gerente, o restaurante ativo é o dele
            if (utilizadorLogado.getFuncao() == Funcao.GERENTE) {
                this.restauranteAtivoId = utilizadorLogado.getRestauranteId();
            }
            return true;
        }
        return false;
    }

    public boolean ehCOO() { return utilizadorLogado != null && utilizadorLogado.getFuncao() == Funcao.COO; }
    
    public void selecionarRestaurante(int rId) {
        if (ehCOO()) this.restauranteAtivoId = rId;
    }

    public List<Funcionario> getEquipa() { return facade.listarFuncionarios(utilizadorLogado, restauranteAtivoId); }
    public double getFaturacao() { return facade.consultarFaturacao(utilizadorLogado, restauranteAtivoId); }
    public void enviarMensagem(String txt, boolean urg) { facade.enviarAvisoCozinha(utilizadorLogado, restauranteAtivoId, txt, urg); }
    public List<Restaurante> getTodosRestaurantes() { return facade.listarTodosRestaurantes(utilizadorLogado); }
    public int getRestauranteAtivoId() { return restauranteAtivoId; }
}
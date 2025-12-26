package dss2526.ui.controller;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.Funcao;
import dss2526.domain.enumeration.Trabalho;
import dss2526.service.gestao.GestaoFacade;
import dss2526.service.gestao.IGestaoFacade;
import java.util.List;

public class GestaoController {
    private final IGestaoFacade facade;
    private Funcionario utilizadorLogado;
    private int restauranteAtivoId = -1;

    public GestaoController() {
        // Usa o Singleton da Facade que estende a BaseFacade
        this.facade = GestaoFacade.getInstance();
    }

    /**
     * Tenta efetuar o login e fixa o restaurante se for Gerente.
     */
    public boolean login(String u, String p) {
        this.utilizadorLogado = facade.login(u, p);
        if (this.utilizadorLogado != null) {
            if (utilizadorLogado.getFuncao() == Funcao.GERENTE) {
                this.restauranteAtivoId = utilizadorLogado.getRestauranteId();
            }
            return true;
        }
        return false;
    }

    /**
     * Retorna o cargo (Função) do utilizador atual para a UI.
     */
    public String getCargoUtilizador() {
        if (utilizadorLogado != null) {
            return utilizadorLogado.getFuncao().name();
        }
        return "NENHUM";
    }

    public boolean ehCOO() {
        return utilizadorLogado != null && utilizadorLogado.getFuncao() == Funcao.COO;
    }

    public void selecionarRestaurante(int rId) {
        if (ehCOO()) {
            this.restauranteAtivoId = rId;
        }
    }

    // --- MÉTODOS DE OPERAÇÃO (Delegam para a Facade) ---

    public List<Funcionario> getEquipa() {
        return facade.listarFuncionarios(utilizadorLogado, restauranteAtivoId);
    }

    public void contratar(Funcionario novo) {
        // Garante que o novo funcionário é associado ao restaurante que estamos a gerir
        novo.setRestauranteId(restauranteAtivoId);
        facade.contratarFuncionario(utilizadorLogado, novo);
    }

    public void demitir(int fId) {
        facade.demitirFuncionario(utilizadorLogado, fId);
    }

    public void atualizarStock(int ingId, float qtd) {
        facade.atualizarStock(utilizadorLogado, restauranteAtivoId, ingId, qtd);
    }

    public void enviarMensagem(String txt, boolean urg) {
        facade.enviarAvisoCozinha(utilizadorLogado, restauranteAtivoId, txt, urg);
    }

    public double getFaturacao() {
        return facade.consultarFaturacao(utilizadorLogado, restauranteAtivoId);
    }

    public void adicionarEstacaoTrabalho(Trabalho t) {
        facade.adicionarEstacao(utilizadorLogado, restauranteAtivoId, t);
    }

    public List<Restaurante> getTodosRestaurantes() {
        return facade.listarTodosRestaurantes(utilizadorLogado);
    }

    public int getRestauranteAtivoId() {
        return restauranteAtivoId;
    }

    public void encerrarSessao() {
        this.utilizadorLogado = null;
        this.restauranteAtivoId = -1;
    }
}
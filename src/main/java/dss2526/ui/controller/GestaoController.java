package dss2526.ui.controller;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.Funcao;
import dss2526.domain.enumeration.Trabalho;
import dss2526.service.gestao.GestaoFacade;
import dss2526.service.gestao.IGestaoFacade;

import java.util.List;

public class GestaoController {
    private final IGestaoFacade facade;
    
    // ESTADO DA SESSÃO (Stateful)
    private Funcionario utilizadorLogado;
    private int restauranteSelecionadoId = -1;

    public GestaoController(IGestaoFacade facade) {
        this.facade = facade;
    }

    public GestaoController() {
        this.facade = GestaoFacade.getInstance();
    }

    // --- Autenticação ---

    public boolean iniciarSessao(String user, String pass) {
        this.utilizadorLogado = facade.login(user, pass);
        if (this.utilizadorLogado != null) {
            // Lógica automática: Se for Gerente, fixa logo o restaurante dele
            if (this.utilizadorLogado.getFuncao() == Funcao.GERENTE) {
                this.restauranteSelecionadoId = this.utilizadorLogado.getRestauranteId();
            }
            return true;
        }
        return false;
    }

    public void encerrarSessao() {
        facade.logout();
        this.utilizadorLogado = null;
        this.restauranteSelecionadoId = -1;
    }

    // --- Verificações de Perfil (Para a UI saber o que mostrar) ---

    public boolean ehCOO() {
        return utilizadorLogado != null && utilizadorLogado.getFuncao() == Funcao.COO;
    }

    public String getCargoUtilizador() {
        if (utilizadorLogado != null) { return utilizadorLogado.getFuncao().name();}
        return "NENHUM";
    }

    // --- Gestão de Restaurantes (COO) ---

    public List<Restaurante> listarTodosRestaurantes() {
        return facade.listarTodosRestaurantes(utilizadorLogado);
    }

    public void selecionarRestaurante(int rId) {
        if (ehCOO()) {
            this.restauranteSelecionadoId = rId;
        }
    }

    // --- Operações de Unidade (Usam o restauranteSelecionadoId) ---

    public List<Funcionario> getEquipa() {
        return facade.listarFuncionarios(utilizadorLogado, restauranteSelecionadoId);
    }

    public double getFaturacao() {
        return facade.consultarFaturacao(utilizadorLogado, restauranteSelecionadoId);
    }

    public void contratar(Funcionario novo) {
        // Garante que o novo funcionário vai para o restaurante que está a ser gerido
        novo.setRestauranteId(restauranteSelecionadoId);
        facade.contratarFuncionario(utilizadorLogado, novo);
    }

    public void demitir(int fId) {
        facade.demitirFuncionario(utilizadorLogado, fId);
    }

    public void atualizarStock(int ingredienteId, float quantidade) {
        facade.atualizarStock(utilizadorLogado, restauranteSelecionadoId, ingredienteId, quantidade);
    }

    public void enviarMensagemCozinha(String msg, boolean urgente) {
        facade.enviarAvisoCozinha(utilizadorLogado, restauranteSelecionadoId, msg, urgente);
    }

    public void adicionarEstacaoTrabalho(Trabalho tipo) {
        facade.adicionarEstacao(utilizadorLogado, restauranteSelecionadoId, tipo);
    }

    public int getRestauranteAtivoId() {
        return restauranteSelecionadoId;
    }
}
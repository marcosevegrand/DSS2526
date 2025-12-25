package dss2526.ui.controller;

import dss2526.domain.entity.*;
import dss2526.service.gestao.GestaoFacade;
import dss2526.service.gestao.IGestaoFacade;
import java.util.List;

public class GestaoController {

    private IGestaoFacade gestaoFacade;

    public GestaoController() {
        // Connect to the Singleton Facade
        this.gestaoFacade = GestaoFacade.getInstance();
    }

    public void adicionarFuncionario(String nome, String user, String pass, String funcao) {
        Funcionario f = new Funcionario();
        // set fields...
        gestaoFacade.registarFuncionario(f);
    }

    public List<Menu> getMenus() {
        return gestaoFacade.getMenus();
    }
    
    // Wrapper methods for the UI to call
}
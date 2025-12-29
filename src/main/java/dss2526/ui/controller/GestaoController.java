package dss2526.ui.controller;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.Funcao;
import dss2526.service.gestao.GestaoFacade;
import dss2526.service.gestao.IGestaoFacade;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class GestaoController {
    private final IGestaoFacade facade;
    
    // Estado da Sessao
    private int utilizadorId = -1;
    private Funcao roleUtilizador;
    private int restauranteAtivoId = -1;

    // Caches de IDs para mapeamento UI
    private List<Integer> cacheIdsRestaurantes = new ArrayList<>();
    private List<Integer> cacheIdsFuncionarios = new ArrayList<>();
    private List<Integer> cacheIdsIngredientes = new ArrayList<>();

    public GestaoController() {
        this.facade = GestaoFacade.getInstance();
    }

    public boolean autenticar(String u, String p) {
        Funcionario f = facade.login(u, p);
        if (f != null && f.getPassword().equals(p)) {
            this.utilizadorId = f.getId();
            this.roleUtilizador = f.getFuncao();
            if (f.getRestauranteId() != null) this.restauranteAtivoId = f.getRestauranteId();
            return true;
        }
        return false;
    }

    public boolean ehCOO() { return roleUtilizador == Funcao.COO; }
    public boolean ehGerente() { return roleUtilizador == Funcao.GERENTE; }

    // --- Restaurantes ---
    public List<String> listarRestaurantes() {
        List<Restaurante> lista = facade.listarRestaurantes();
        this.cacheIdsRestaurantes = lista.stream().map(Restaurante::getId).collect(Collectors.toList());
        return lista.stream().map(r -> r.getNome() + " (" + r.getLocalizacao() + ")").collect(Collectors.toList());
    }

    public void selecionarRestaurante(int index) {
        this.restauranteAtivoId = cacheIdsRestaurantes.get(index);
    }

    // --- Funcionarios ---
    public List<String> listarFuncionarios() {
        List<Funcionario> lista = facade.listarFuncionariosDeRestaurante(restauranteAtivoId);
        this.cacheIdsFuncionarios = lista.stream().map(Funcionario::getId).collect(Collectors.toList());
        return lista.stream().map(f -> f.getUtilizador() + " [" + f.getFuncao() + "]").collect(Collectors.toList());
    }

    public void contratar(String user, String pass, int cargo) {
        Funcionario f = new Funcionario();
        f.setUtilizador(user); f.setPassword(pass);
        f.setFuncao(cargo == 2 ? Funcao.GERENTE : Funcao.FUNCIONARIO);
        facade.contratarFuncionario(utilizadorId, restauranteAtivoId, f);
    }

    public void demitir(int index) {
        facade.demitirFuncionario(utilizadorId, cacheIdsFuncionarios.get(index));
    }

    // --- Stock ---
    public List<String> listarIngredientes() {
        List<Ingrediente> lista = facade.listarIngredientes();
        this.cacheIdsIngredientes = lista.stream().map(Ingrediente::getId).collect(Collectors.toList());
        return lista.stream().map(Ingrediente::getNome).collect(Collectors.toList());
    }

    public void atualizarStock(int index, int qtd) {
        facade.atualizarStock(utilizadorId, restauranteAtivoId, cacheIdsIngredientes.get(index), qtd);
    }

    // --- Estatisticas ---
    public String obterDadosEstatisticos(LocalDateTime inicio, LocalDateTime fim) {
        StringBuilder sb = new StringBuilder();
        sb.append("Faturacao: ").append(facade.consultarFaturacao(restauranteAtivoId, inicio, fim)).append(" EUR\n");
        sb.append("Tempo Medio Espera: ").append(facade.consultarTempoMedioEspera(restauranteAtivoId, inicio, fim)).append(" min\n");
        
        sb.append("Volume por Estado:\n");
        facade.consultarVolumePedidos(restauranteAtivoId, inicio, fim).forEach((k, v) -> sb.append("- ").append(k).append(": ").append(v).append("\n"));
        
        sb.append("Top Produtos:\n");
        facade.consultarTopProdutos(restauranteAtivoId, inicio, fim).forEach((k, v) -> sb.append("- ").append(k).append(": ").append(v).append("\n"));
        
        return sb.toString();
    }
}
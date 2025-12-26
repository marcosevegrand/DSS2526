package dss2526.ui.controller;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.Funcao;
import dss2526.domain.enumeration.Trabalho;
import dss2526.service.gestao.GestaoFacade;
import dss2526.service.gestao.IGestaoFacade;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GestaoController {
    
    private final IGestaoFacade facade;
    
    // Estado da Sessão
    private Funcionario utilizadorLogado;
    private int restauranteAtivoId = -1; 

    public GestaoController() {
        this.facade = GestaoFacade.getInstance();
    }

    // --- Autenticação e Contexto ---

    public boolean login(String u, String p) {
        this.utilizadorLogado = facade.login(u, p);
        if (this.utilizadorLogado != null) {
            if (utilizadorLogado.getRestauranteId() != null) {
                this.restauranteAtivoId = utilizadorLogado.getRestauranteId();
            }
            return true;
        }
        return false;
    }

    public void logout() {
        this.utilizadorLogado = null;
        this.restauranteAtivoId = -1;
    }

    public boolean isCOO() {
        return utilizadorLogado != null && utilizadorLogado.getFuncao() == Funcao.COO;
    }

    public boolean isGerente() {
        return utilizadorLogado != null && utilizadorLogado.getFuncao() == Funcao.GERENTE;
    }

    public String getNomeUtilizador() {
        return utilizadorLogado != null ? utilizadorLogado.getUtilizador() : "Visitante";
    }

    public void selecionarRestauranteContexto(int rId) {
        if (isCOO()) {
            this.restauranteAtivoId = rId;
        }
    }

    public String getNomeRestauranteAtivo() {
        if (restauranteAtivoId == -1) return "Nenhum";
        Restaurante r = facade.obterRestaurante(restauranteAtivoId);
        return r != null ? r.getNome() : "Desconhecido";
    }

    // --- Listagens Globais (para seleção na UI) ---

    public List<Ingrediente> listarTodosIngredientes() {
        return facade.listarIngredientes();
    }

    public List<Passo> listarTodosPassos() {
        return facade.listarPassos();
    }

    public List<Produto> listarTodosProdutos() {
        return facade.listarProdutos();
    }

    public List<Catalogo> listarTodosCatalogos() {
        return facade.listarCatalogos();
    }

    // --- Operações Globais (COO) ---

    public List<String> listarRestaurantes() {
        return facade.listarRestaurantes().stream()
                .map(r -> String.format("%d. %s (%s)", r.getId(), r.getNome(), r.getLocalizacao()))
                .collect(Collectors.toList());
    }

    public void criarRestaurante(String nome, String local) {
        facade.criarRestaurante(utilizadorLogado, nome, local);
    }

    public void criarIngrediente(String nome, String unidade, String alergenico) {
        Ingrediente i = new Ingrediente();
        i.setNome(nome);
        i.setUnidade(unidade);
        i.setAlergenico(alergenico.isBlank() ? null : alergenico);
        facade.criarIngrediente(utilizadorLogado, i);
    }

    public void criarPasso(String nome, long duracaoMinutos, Trabalho trabalho, List<Integer> ingredientesIds) {
        Passo p = new Passo();
        p.setNome(nome);
        p.setDuracao(Duration.ofMinutes(duracaoMinutos));
        p.setTrabalho(trabalho);
        p.setIngredienteIds(ingredientesIds);
        facade.criarPasso(utilizadorLogado, p);
    }

    public void criarProduto(String nome, double preco, List<Integer> passosIds, List<Integer> ingredientesIds, List<Integer> quantidades) {
        Produto p = new Produto();
        p.setNome(nome);
        p.setPreco(preco);
        p.setPassoIds(passosIds);
        
        // Criar Linhas de Produto (Receita)
        for (int i = 0; i < ingredientesIds.size(); i++) {
            LinhaProduto lp = new LinhaProduto();
            lp.setIngredienteId(ingredientesIds.get(i));
            lp.setQuantidade(quantidades.get(i)); // Assumindo indices alinhados pela UI
            p.addLinha(lp);
        }
        
        facade.criarProduto(utilizadorLogado, p);
    }

    public void criarMenu(String nome, double preco, List<Integer> produtosIds) {
        Menu m = new Menu();
        m.setNome(nome);
        m.setPreco(preco);
        for (Integer pId : produtosIds) {
            LinhaMenu lm = new LinhaMenu();
            lm.setProdutoId(pId);
            lm.setQuantidade(1); // Simplificação: 1 unidade de cada produto selecionado
            m.addLinha(lm);
        }
        facade.criarMenu(utilizadorLogado, m);
    }

    public void criarCatalogo(String nome) {
        facade.criarCatalogo(utilizadorLogado, nome);
    }

    // --- Operações Locais (Restaurante Ativo) ---

    public List<String> listarFuncionariosLocais() {
        if (restauranteAtivoId == -1) return List.of();
        return facade.listarFuncionariosDeRestaurante(restauranteAtivoId).stream()
                .map(f -> String.format("ID: %d | %s | %s", f.getId(), f.getUtilizador(), f.getFuncao()))
                .collect(Collectors.toList());
    }

    public void contratarFuncionario(String user, String pass, Funcao funcao) {
        Funcionario f = new Funcionario();
        f.setUtilizador(user);
        f.setPassword(pass);
        f.setFuncao(funcao);
        f.setRestauranteId(restauranteAtivoId);
        facade.contratarFuncionario(utilizadorLogado, f);
    }

    public void demitirFuncionario(int idFunc) {
        facade.demitirFuncionario(utilizadorLogado, idFunc);
    }

    public List<String> listarEstacoesLocais() {
        return facade.listarEstacoesDeRestaurante(restauranteAtivoId).stream()
                .map(e -> String.format("ID: %d | %s", e.getId(), e.getTrabalho()))
                .collect(Collectors.toList());
    }

    public void adicionarEstacao(Trabalho t) {
        facade.adicionarEstacao(utilizadorLogado, restauranteAtivoId, t);
    }

    public void atualizarStock(int idIngrediente, int qtdAdicionar) {
        facade.atualizarStock(utilizadorLogado, restauranteAtivoId, idIngrediente, qtdAdicionar);
    }

    public void enviarMensagem(String txt, boolean urgente) {
        facade.enviarAvisoCozinha(utilizadorLogado, restauranteAtivoId, txt, urgente);
    }

    public void mudarCatalogoRestaurante(int catalogoId) {
        facade.alterarCatalogoRestaurante(utilizadorLogado, restauranteAtivoId, catalogoId);
    }

    // --- Estatísticas ---

    public String getRelatorioFinanceiro() {
        double total = facade.consultarFaturacaoTotal(utilizadorLogado, restauranteAtivoId);
        return String.format("💰 Faturação Total Acumulada: %.2f €", total);
    }

    public List<String> getTopProdutos() {
        Map<String, Integer> top = facade.consultarProdutosMaisVendidos(utilizadorLogado, restauranteAtivoId);
        return top.entrySet().stream()
                .map(e -> String.format("📦 %s: %d vendidos", e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
package dss2526.ui.controller;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;
import dss2526.service.gestao.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class GestaoController {
    private final IGestaoFacade facade = GestaoFacade.getInstance();
    private Funcionario sessao;
    private int restauranteAtualId = -1;
    private List<Integer> cacheIngIds = new ArrayList<>();
    private List<Integer> cacheRestIds = new ArrayList<>();
    private List<Integer> cacheFuncIds = new ArrayList<>();
    private List<Integer> cacheEstIds = new ArrayList<>();

    public boolean autenticar(String u, String p) {
        this.sessao = facade.autenticarFuncionario(u, p);
        if (this.sessao != null && this.sessao.getFuncao() == Funcao.GERENTE && this.sessao.getRestauranteId() != null) this.restauranteAtualId = this.sessao.getRestauranteId();
        return sessao != null;
    }

    public boolean isCOO() { return sessao != null && sessao.getFuncao() == Funcao.COO; }
    public String getNomeUtilizador() { return sessao != null ? sessao.getUtilizador() : "Desconhecido"; }

    public List<String> getRestaurantes() {
        List<Restaurante> l = facade.listarRestaurantes();
        cacheRestIds = l.stream().map(Restaurante::getId).collect(Collectors.toList());
        return l.stream().map(r -> r.getNome() + " (" + r.getLocalizacao() + ")").collect(Collectors.toList());
    }
    public void definirRestauranteAtual(int idx) { if (idx >= 0 && idx < cacheRestIds.size()) this.restauranteAtualId = cacheRestIds.get(idx); }
    public void limparRestauranteAtual() { if (isCOO()) this.restauranteAtualId = -1; }
    public boolean temRestauranteSelecionado() { return this.restauranteAtualId != -1; }

    public List<String> getIngredientes() {
        List<Ingrediente> list = facade.listarIngredientes();
        cacheIngIds = list.stream().map(Ingrediente::getId).collect(Collectors.toList());
        return list.stream().map(i -> i.getNome() + " [" + i.getUnidade() + "]").collect(Collectors.toList());
    }

    public List<String> getNomesFuncionarios() {
        if (restauranteAtualId == -1) return new ArrayList<>();
        List<Funcionario> lista = facade.listarFuncionariosPorRestaurante(restauranteAtualId);
        lista.removeIf(f -> f.getId() == sessao.getId());
        this.cacheFuncIds = lista.stream().map(Funcionario::getId).collect(Collectors.toList());
        return lista.stream().map(f -> f.getUtilizador() + " (" + f.getFuncao() + ")").collect(Collectors.toList());
    }

    public List<String> getNomesEstacoes() {
        if (restauranteAtualId == -1) return new ArrayList<>();
        List<Estacao> lista = facade.listarEstacoesPorRestaurante(restauranteAtualId);
        this.cacheEstIds = lista.stream().map(Estacao::getId).collect(Collectors.toList());
        return lista.stream().map(e -> e.getNome() + (e instanceof Estacao.Caixa ? " [CAIXA]" : " [COZINHA]")).collect(Collectors.toList());
    }

    public void atualizarStock(int idx, int delta) { if (idx >= 0 && idx < cacheIngIds.size() && restauranteAtualId != -1) facade.atualizarStockIngrediente(restauranteAtualId, cacheIngIds.get(idx), delta); }

    public String obterDashboard(String inicioStr, String fimStr) {
        if (restauranteAtualId == -1) return "Nenhum restaurante selecionado.";
        LocalDateTime inicio = null, fim = null;
        try {
            if (inicioStr != null && !inicioStr.isBlank()) inicio = LocalDate.parse(inicioStr.trim()).atStartOfDay();
            if (fimStr != null && !fimStr.isBlank()) fim = LocalDate.parse(fimStr.trim()).atTime(23, 59, 59);
        } catch (DateTimeParseException e) { return "Erro: Formato de data inválido. Utilize o formato AAAA-MM-DD (ex: 2025-10-30)."; }
        return facade.obterDashboardEstatisticas(restauranteAtualId, inicio, fim);
    }

    public void contratarFuncionario(String u, String p, Funcao f) {
        if (restauranteAtualId != -1) { Funcionario n = new Funcionario(); n.setUtilizador(u); n.setPassword(p); n.setFuncao(f); facade.contratarFuncionario(restauranteAtualId, n); }
    }
    public void demitirFuncionario(int idx) { if (idx >= 0 && idx < cacheFuncIds.size()) facade.demitirFuncionario(cacheFuncIds.get(idx)); }

    public void adicionarEstacao(String n, boolean isCaixa) {
        if (restauranteAtualId != -1) { Estacao e = isCaixa ? new Estacao.Caixa() : new Estacao.Cozinha(); e.setNome(n); e.setRestauranteId(restauranteAtualId); facade.adicionarEstacaoTrabalho(e); }
    }
    public void removerEstacao(int idx) { if (idx >= 0 && idx < cacheEstIds.size()) facade.removerEstacaoTrabalho(cacheEstIds.get(idx)); }

    public void enviarMensagemLocal(String texto) { if (restauranteAtualId != -1) facade.enviarMensagemRestaurante(restauranteAtualId, texto, sessao.getUtilizador()); }
    public void enviarMensagemGlobal(String texto) { if (isCOO()) facade.difundirMensagemGlobal(texto, sessao.getUtilizador()); }
}
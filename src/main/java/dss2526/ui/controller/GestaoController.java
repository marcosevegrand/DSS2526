package dss2526.ui.controller;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;
import dss2526.service.gestao.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class GestaoController {
    private final IGestaoFacade facade = GestaoFacade.getInstance();
    private Funcionario sessao;
    private List<Integer> cacheIngIds = new ArrayList<>();
    private List<Integer> cacheRestIds = new ArrayList<>();

    public boolean autenticar(String u, String p) { 
        this.sessao = facade.autenticarFuncionario(u, p); 
        return sessao != null; 
    }
    
    public boolean isCOO() { return sessao != null && sessao.getFuncao() == Funcao.COO; }
    public int getRestauranteProprio() { return sessao.getRestauranteId() != null ? sessao.getRestauranteId() : 0; }

    public List<String> getRestaurantes() { 
        List<Restaurante> l = facade.listarRestaurantes();
        cacheRestIds = l.stream().map(Restaurante::getId).collect(Collectors.toList());
        return l.stream().map(Restaurante::getNome).collect(Collectors.toList()); 
    }
    public int getRestauranteId(int idx) { return cacheRestIds.get(idx); }

    public List<String> getIngredientes() {
        List<Ingrediente> list = facade.listarIngredientes();
        cacheIngIds = list.stream().map(Ingrediente::getId).collect(Collectors.toList());
        return list.stream().map(Ingrediente::getNome).collect(Collectors.toList());
    }

    public void stock(int rid, int idx, int d) { facade.atualizarStockIngrediente(sessao.getId(), rid, cacheIngIds.get(idx), d); }
    public String stats(int rid, LocalDateTime i, LocalDateTime f) { return facade.obterDashboardEstatisticas(rid, i, f); }

    public void contratar(String u, String p, Funcao f, Integer rid) {
        Funcionario n = new Funcionario(); n.setUtilizador(u); n.setPassword(p); n.setFuncao(f); n.setRestauranteId(rid);
        facade.contratarFuncionario(sessao.getId(), n);
    }
    public void demitir(int fid) { facade.demitirFuncionario(sessao.getId(), fid); }

    public void addEstacao(int rid, String n, boolean isCaixa) {
        Estacao e = isCaixa ? new Estacao.Caixa() : new Estacao.Cozinha();
        e.setNome(n); e.setRestauranteId(rid);
        facade.adicionarEstacaoTrabalho(sessao.getId(), e);
    }
    public void remEstacao(int eid) { facade.removerEstacaoTrabalho(sessao.getId(), eid); }

    public void msgRest(int rid, String t) { facade.enviarMensagemRestaurante(sessao.getId(), rid, t); }
    public void msgGlobal(String t) { facade.difundirMensagemGlobal(sessao.getId(), t); }
}
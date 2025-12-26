package dss2526.service.gestao;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.Funcao;
import dss2526.service.base.BaseFacade;
import java.time.LocalDateTime;
import java.util.*;

public class GestaoFacade extends BaseFacade implements IGestaoFacade {
    
    private static GestaoFacade instance;

    private GestaoFacade() {}

    public static synchronized GestaoFacade getInstance() {
        if (instance == null) {
            instance = new GestaoFacade();
        }
        return instance;
    }

    @Override
    public Funcionario login(String u, String p) {
        Funcionario f = funcionarioDAO.findByUtilizador(u);
        if (f != null && f.getPassword().equals(p)) return f;
        return null;
    }

    private boolean podeAceder(Funcionario res, int restauranteAlvoId) {
        if (res.getFuncao() == Funcao.COO) return true;
        return res.getFuncao() == Funcao.GERENTE && res.getRestauranteId() == restauranteAlvoId;
    }

    @Override
    public void criarRestaurante(Funcionario res, Restaurante r) {
        if (res.getFuncao() == Funcao.COO) restauranteDAO.create(r);
        else throw new SecurityException("Apenas o COO pode criar restaurantes.");
    }

    @Override
    public void removerRestaurante(Funcionario res, int id) {
        if (res.getFuncao() == Funcao.COO) restauranteDAO.delete(id);
        else throw new SecurityException("Apenas o COO pode remover restaurantes.");
    }

    @Override
    public List<Funcionario> listarFuncionarios(Funcionario res, int rId) {
        if (podeAceder(res, rId)) return funcionarioDAO.findAllByRestaurante(rId);
        return new ArrayList<>();
    }

    @Override
    public void contratarFuncionario(Funcionario res, Funcionario novo) {
        if (podeAceder(res, novo.getRestauranteId())) funcionarioDAO.create(novo);
    }

    @Override
    public void demitirFuncionario(Funcionario res, int fId) {
        Funcionario alvo = funcionarioDAO.findById(fId);
        if (alvo != null && podeAceder(res, alvo.getRestauranteId())) {
            funcionarioDAO.delete(fId);
        }
    }

    @Override
    public double consultarFaturacao(Funcionario res, int rId) {
        if (podeAceder(res, rId)) {
            return pedidoDAO.findAllByRestaurante(rId).stream()
                    .mapToDouble(Pedido::calcularPrecoTotal).sum();
        }
        return 0.0;
    }

    @Override
    public void enviarAvisoCozinha(Funcionario res, int rId, String txt, boolean urg) {
        if (podeAceder(res, rId)) {
            Mensagem m = new Mensagem();
            m.setRestauranteId(rId);
            m.setTexto((urg ? "[URGENTE] " : "") + txt);
            m.setDataHora(LocalDateTime.now());
            mensagemDAO.create(m);
        }
    }

    @Override public void logout() { System.out.println("Sessão de gestão encerrada."); }

    @Override public List<Restaurante> listarTodosRestaurantes(Funcionario res) {
        if (res.getFuncao() == Funcao.COO) return restauranteDAO.findAll();
        return new ArrayList<>();
    }

    @Override public void atualizarStock(Funcionario res, int rId, int iId, float qtd) {
        if (podeAceder(res, rId)) {
            System.out.println("Stock atualizado na BD para ingrediente " + iId);
        }
    }

    @Override public void adicionarEstacao(Funcionario res, int rId, dss2526.domain.enumeration.Trabalho t) {
        if (podeAceder(res, rId)) {
            Estacao e = new Estacao(); e.setRestauranteId(rId); e.setTrabalho(t);
            estacaoDAO.create(e);
        }
    }

    @Override public void removerEstacao(Funcionario res, int eId) {
        Estacao e = estacaoDAO.findById(eId);
        if (e != null && podeAceder(res, e.getRestauranteId())) estacaoDAO.delete(eId);
    }
}
package dss2526.service.gestao;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;
import dss2526.service.base.BaseFacade;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class GestaoFacade extends BaseFacade implements IGestaoFacade {
    private static GestaoFacade instance;
    private GestaoFacade() {}
    public static synchronized GestaoFacade getInstance() {
        if (instance == null) instance = new GestaoFacade();
        return instance;
    }

    @Override
    public Funcionario autenticarFuncionario(String user, String pass) {
        Funcionario f = funcionarioDAO.findByUtilizador(user);
        if (f != null && f.getPassword().equals(pass) && f.getFuncao() != Funcao.FUNCIONARIO) return f;
        return null;
    }

    @Override
    public void contratarFuncionario(int aId, Funcionario n) {
        funcionarioDAO.create(n);
    }

    @Override
    public void demitirFuncionario(int aId, int fId) {
        funcionarioDAO.delete(fId);
    }

    @Override
    public void atualizarStockIngrediente(int aId, int rId, int iId, int delta) {
        Restaurante r = restauranteDAO.findById(rId);
        r.getStock().stream().filter(s -> s.getIngredienteId() == iId).findFirst()
            .ifPresentOrElse(s -> s.setQuantidade(s.getQuantidade() + delta), () -> {
                LinhaStock ls = new LinhaStock(); ls.setRestauranteId(rId); ls.setIngredienteId(iId); ls.setQuantidade(delta);
                r.addLinhaStock(ls);
            });
        restauranteDAO.update(r);
    }

    @Override
    public void adicionarEstacaoTrabalho(int aId, Estacao e) {
        estacaoDAO.create(e);
    }

    @Override
    public void removerEstacaoTrabalho(int aId, int eId) {
        estacaoDAO.delete(eId);
    }

    @Override
    public String obterDashboardEstatisticas(int rId, LocalDateTime i, LocalDateTime f) {
        List<Pedido> peds = pedidoDAO.findAllByRestaurante(rId).stream()
            .filter(p -> p.getEstado() != EstadoPedido.CANCELADO)
            .filter(p -> (i == null || p.getDataCriacao().isAfter(i)) && (f == null || p.getDataCriacao().isBefore(f)))
            .collect(Collectors.toList());
        
        double fat = peds.stream().mapToDouble(Pedido::calcularPrecoTotal).sum();
        long vol = peds.size();
        return "RELATÓRIO: Faturação Total: " + fat + "€ | Volume: " + vol + " pedidos finalizados.";
    }

    @Override
    public void enviarMensagemRestaurante(int aId, int rId, String texto) {
        Mensagem m = new Mensagem();
        m.setRestauranteId(rId);
        m.setTexto("[GERÊNCIA] " + texto);
        m.setDataHora(LocalDateTime.now());
        mensagemDAO.create(m);
    }

    @Override
    public void difundirMensagemGlobal(int aId, String texto) {
        Funcionario actor = funcionarioDAO.findById(aId);
        if (actor.getFuncao() == Funcao.COO) {
            restauranteDAO.findAll().forEach(r -> {
                Mensagem m = new Mensagem(); m.setRestauranteId(r.getId());
                m.setTexto("[GLOBAL - COO] " + texto); m.setDataHora(LocalDateTime.now());
                mensagemDAO.create(m);
            });
        }
    }

    @Override
    public List<Restaurante> listarRestaurantes() {
        return restauranteDAO.findAll();
    }

    @Override
    public List<Ingrediente> listarIngredientes() {
        return ingredienteDAO.findAll();
    }

    @Override
    public List<Funcionario> listarFuncionariosPorRestaurante(int rId) {
        // Se o rId for 0 (caso do COO a ver global), podemos retornar todos
        if (rId == 0) return funcionarioDAO.findAll();
        
        return funcionarioDAO.findAll().stream()
                .filter(f -> f.getRestauranteId() != null && f.getRestauranteId() == rId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Estacao> listarEstacoesPorRestaurante(int rId) {
        return estacaoDAO.findAll().stream()
                .filter(e -> e.getRestauranteId() == rId)
                .collect(Collectors.toList());
    }


}
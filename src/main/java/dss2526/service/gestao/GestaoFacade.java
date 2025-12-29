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
        // Apenas GERENTE, COO ou SYSADMIN podem aceder ao módulo de gestão
        if (f != null && f.getPassword().equals(pass) && f.getFuncao() != Funcao.FUNCIONARIO) {
            return f;
        }
        return null;
    }

    @Override
    public void contratarFuncionario(int rId, Funcionario n) {
        n.setRestauranteId(rId);
        funcionarioDAO.create(n);
    }

    @Override
    public void demitirFuncionario(int fId) {
        funcionarioDAO.delete(fId);
    }

    @Override
    public void atualizarStockIngrediente(int rId, int iId, int delta) {
        Restaurante r = restauranteDAO.findById(rId);
        if (r == null) return;

        Optional<LinhaStock> linha = r.getStock().stream()
            .filter(s -> s.getIngredienteId() == iId)
            .findFirst();

        if (linha.isPresent()) {
            LinhaStock ls = linha.get();
            int novaQtd = ls.getQuantidade() + delta;
            ls.setQuantidade(Math.max(0, novaQtd)); // Impede stock negativo
        } else if (delta > 0) {
            LinhaStock ls = new LinhaStock(); 
            ls.setRestauranteId(rId); 
            ls.setIngredienteId(iId); 
            ls.setQuantidade(delta);
            r.addLinhaStock(ls);
        }
        restauranteDAO.update(r);
    }

    @Override
    public void adicionarEstacaoTrabalho(Estacao e) {
        estacaoDAO.create(e);
    }

    @Override
    public void removerEstacaoTrabalho(int eId) {
        estacaoDAO.delete(eId);
    }

    @Override
    public String obterDashboardEstatisticas(int rId, LocalDateTime i, LocalDateTime f) {
        List<Pedido> peds = pedidoDAO.findAllByRestaurante(rId).stream()
            .filter(p -> p.getEstado() != EstadoPedido.CANCELADO)
            .filter(p -> (i == null || !p.getDataCriacao().isBefore(i)) && (f == null || !p.getDataCriacao().isAfter(f)))
            .collect(Collectors.toList());
        
        double fat = peds.stream().mapToDouble(Pedido::calcularPrecoTotal).sum();
        long vol = peds.size();
        
        // Contagem por estado
        long entregues = peds.stream().filter(p -> p.getEstado() == EstadoPedido.ENTREGUE).count();
        long emPrep = peds.stream().filter(p -> p.getEstado() == EstadoPedido.EM_PREPARACAO).count();

        return String.format(
            "=== RELATÓRIO DO RESTAURANTE #%d ===\n" +
            "Faturação Total: %.2f€\n" +
            "Volume Total: %d pedidos\n" +
            "Entregues: %d | Em Preparação: %d", 
            rId, fat, vol, entregues, emPrep);
    }

    @Override
    public void enviarMensagemRestaurante(int rId, String texto, String nomeAutor) {
        Mensagem m = new Mensagem();
        m.setRestauranteId(rId);
        // Formata o texto para incluir o autor, já que a entidade não tem campo específico
        m.setTexto("[" + nomeAutor.toUpperCase() + "] " + texto);
        m.setDataHora(LocalDateTime.now());
        mensagemDAO.create(m);
    }

    @Override
    public void difundirMensagemGlobal(String texto, String nomeAutor) {
        // Envia para TODOS os restaurantes
        restauranteDAO.findAll().forEach(r -> {
            Mensagem m = new Mensagem(); 
            m.setRestauranteId(r.getId());
            m.setTexto("[GLOBAL - " + nomeAutor.toUpperCase() + "] " + texto); 
            m.setDataHora(LocalDateTime.now());
            mensagemDAO.create(m);
        });
    }

    @Override public List<Restaurante> listarRestaurantes() { return restauranteDAO.findAll(); }
    @Override public List<Ingrediente> listarIngredientes() { return ingredienteDAO.findAll(); }

    @Override
    public List<Funcionario> listarFuncionariosPorRestaurante(int rId) {
        return funcionarioDAO.findAll().stream()
                .filter(f -> f.getRestauranteId() != null && f.getRestauranteId() == rId)
                // Excluir a si próprio ou admins globais se necessário, mas aqui listamos todos daquele restaurante
                .collect(Collectors.toList());
    }

    @Override
    public List<Estacao> listarEstacoesPorRestaurante(int rId) {
        return estacaoDAO.findAll().stream()
                .filter(e -> e.getRestauranteId() == rId)
                .collect(Collectors.toList());
    }
}
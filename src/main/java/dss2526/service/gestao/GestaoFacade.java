package dss2526.service.gestao;

import dss2526.data.contract.*;
import dss2526.data.impl.*;
import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Facade para o módulo de Gestão.
 * Singleton que oferece operações de gestão de restaurantes, funcionários, estações e stock.
 * 
 * Esta classe implementa APENAS os métodos necessários, não herdando de BaseFacade.
 */
public class GestaoFacade implements IGestaoFacade {
    private static GestaoFacade instance;
    
    // ============ DAOs NECESSÁRIOS (6 de 12) ============
    // Cada DAO é declarado como 'protected final' para imutabilidade e visibilidade
    protected final FuncionarioDAO funcionarioDAO = FuncionarioDAOImpl.getInstance();
    protected final RestauranteDAO restauranteDAO = RestauranteDAOImpl.getInstance();
    protected final IngredienteDAO ingredienteDAO = IngredienteDAOImpl.getInstance();
    protected final EstacaoDAO estacaoDAO = EstacaoDAOImpl.getInstance();
    protected final PedidoDAO pedidoDAO = PedidoDAOImpl.getInstance();
    protected final MensagemDAO mensagemDAO = MensagemDAOImpl.getInstance();
    
    /**
     * Construtor privado (padrão Singleton).
     */
    private GestaoFacade() {}
    
    /**
     * Obtém a instância única (thread-safe).
     */
    public static synchronized GestaoFacade getInstance() {
        if (instance == null) {
            instance = new GestaoFacade();
        }
        return instance;
    }

    // ============ IMPLEMENTAÇÃO DOS MÉTODOS ============
    
    @Override
    public Funcionario autenticarFuncionario(String user, String pass) {
        Funcionario f = funcionarioDAO.findByUtilizador(user);
        // Apenas GERENTE, COO ou SYSADMIN podem aceder à gestão
        if (f != null && f.getPassword().equals(pass) && f.getFuncao() != Funcao.FUNCIONARIO) {
            return f;
        }
        return null;
    }

    @Override
    public void contratarFuncionario(int restauranteId, Funcionario novo) {
        novo.setRestauranteId(restauranteId);
        funcionarioDAO.create(novo);
    }

    @Override
    public void demitirFuncionario(int funcionarioId) {
        funcionarioDAO.delete(funcionarioId);
    }

    @Override
    public List<Funcionario> listarFuncionariosPorRestaurante(int rId) {
        return funcionarioDAO.findAll().stream()
                .filter(f -> f.getRestauranteId() != null && f.getRestauranteId() == rId)
                .collect(Collectors.toList());
    }

    @Override
    public void atualizarStockIngrediente(int restauranteId, int ingredienteId, int delta) {
        Restaurante r = restauranteDAO.findById(restauranteId);
        if (r == null) {
            return;
        }

        Optional<LinhaStock> linha = r.getStock().stream()
            .filter(s -> s.getIngredienteId() == ingredienteId)
            .findFirst();

        if (linha.isPresent()) {
            // Atualiza linha existente
            LinhaStock ls = linha.get();
            int novaQtd = ls.getQuantidade() + delta;
            ls.setQuantidade(Math.max(0, novaQtd)); // Impede stock negativo
        } else if (delta > 0) {
            // Cria nova linha de stock
            LinhaStock ls = new LinhaStock(); 
            ls.setRestauranteId(restauranteId); 
            ls.setIngredienteId(ingredienteId); 
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
    public void removerEstacaoTrabalho(int estacaoId) {
        estacaoDAO.delete(estacaoId);
    }

    @Override
    public List<Estacao> listarEstacoesPorRestaurante(int rId) {
        return estacaoDAO.findAll().stream()
                .filter(e -> e.getRestauranteId() == rId)
                .collect(Collectors.toList());
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
    public String obterDashboardEstatisticas(int restauranteId, LocalDateTime inicio, LocalDateTime fim) {
        // Obtém todos os pedidos do restaurante
        List<Pedido> pedidos = pedidoDAO.findAllByRestaurante(restauranteId).stream()
            // Remove pedidos cancelados
            .filter(p -> p.getEstado() != EstadoPedido.CANCELADO)
            // Filtra por intervalo de datas (se fornecido)
            .filter(p -> (inicio == null || !p.getDataCriacao().isBefore(inicio)) && 
                        (fim == null || !p.getDataCriacao().isAfter(fim)))
            .collect(Collectors.toList());
        
        // Calcula estatísticas
        double faturacao = pedidos.stream()
            .mapToDouble(Pedido::calcularPrecoTotal)
            .sum();
        
        long volume = pedidos.size();
        
        long entregues = pedidos.stream()
            .filter(p -> p.getEstado() == EstadoPedido.ENTREGUE)
            .count();
        
        long emPreparacao = pedidos.stream()
            .filter(p -> p.getEstado() == EstadoPedido.EM_PREPARACAO)
            .count();

        return String.format(
            "=== RELATÓRIO DO RESTAURANTE #%d ===\n" +
            "Fatuação Total: %.2f€\n" +
            "Volume Total: %d pedidos\n" +
            "Entregues: %d | Em Preparação: %d", 
            restauranteId, faturacao, volume, entregues, emPreparacao);
    }

    @Override
    public void enviarMensagemRestaurante(int restauranteId, String texto, String nomeAutor) {
        Mensagem m = new Mensagem();
        m.setRestauranteId(restauranteId);
        // Formata: [AUTOR] Texto
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
}

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
 * Esta classe implementa APENAS os métodos necessários conforme o Interface Segregation Principle.
 */
public class GestaoFacade implements IGestaoFacade {
    private static GestaoFacade instance;

    // ============ DAOs NECESSÁRIOS ============
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
        if (user == null || pass == null || user.isBlank() || pass.isBlank()) {
            return null;
        }

        Funcionario f = funcionarioDAO.findByUtilizador(user);

        // Apenas GERENTE, COO ou SYSADMIN podem aceder à gestão
        if (f != null && f.getPassword().equals(pass) && f.getFuncao() != Funcao.FUNCIONARIO) {
            return f;
        }
        return null;
    }

    @Override
    public void contratarFuncionario(int restauranteId, Funcionario novo) {
        if (novo == null) {
            return;
        }
        novo.setRestauranteId(restauranteId);
        funcionarioDAO.create(novo);
    }

    @Override
    public void demitirFuncionario(int funcionarioId) {
        funcionarioDAO.delete(funcionarioId);
    }

    @Override
    public List<Funcionario> listarFuncionariosPorRestaurante(int rId) {
        return funcionarioDAO.findAllByRestaurante(rId);
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
            // Cria nova linha de stock apenas se o delta for positivo
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
        if (e != null) {
            estacaoDAO.create(e);
        }
    }

    @Override
    public void removerEstacaoTrabalho(int estacaoId) {
        estacaoDAO.delete(estacaoId);
    }

    @Override
    public List<Estacao> listarEstacoesPorRestaurante(int rId) {
        return estacaoDAO.findAllByRestaurante(rId);
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
        Restaurante restaurante = restauranteDAO.findById(restauranteId);
        if (restaurante == null) {
            return "Restaurante não encontrado.";
        }

        // Obtém todos os pedidos do restaurante
        List<Pedido> pedidos = pedidoDAO.findAllByRestaurante(restauranteId).stream()
                // Remove pedidos cancelados
                .filter(p -> p.getEstado() != EstadoPedido.CANCELADO)
                // Filtra por intervalo de datas (se fornecido)
                .filter(p -> {
                    if (p.getDataCriacao() == null) return false;
                    if (inicio != null && p.getDataCriacao().isBefore(inicio)) return false;
                    if (fim != null && p.getDataCriacao().isAfter(fim)) return false;
                    return true;
                })
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

        long prontos = pedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.PRONTO)
                .count();

        long aguardaPagamento = pedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.AGUARDA_PAGAMENTO)
                .count();

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== RELATÓRIO: %s ===\n", restaurante.getNome()));

        if (inicio != null || fim != null) {
            sb.append(String.format("Período: %s a %s\n",
                    inicio != null ? inicio.toLocalDate() : "início",
                    fim != null ? fim.toLocalDate() : "hoje"));
        } else {
            sb.append("Período: Todo o histórico\n");
        }

        sb.append(String.format("\nFaturação Total: %.2f€\n", faturacao));
        sb.append(String.format("Volume Total: %d pedidos\n", volume));
        sb.append(String.format("\n--- Estado dos Pedidos ---\n"));
        sb.append(String.format("Entregues: %d\n", entregues));
        sb.append(String.format("Em Preparação: %d\n", emPreparacao));
        sb.append(String.format("Prontos para Entrega: %d\n", prontos));
        sb.append(String.format("Aguardam Pagamento: %d\n", aguardaPagamento));

        return sb.toString();
    }

    @Override
    public void enviarMensagemRestaurante(int restauranteId, String texto, String nomeAutor) {
        if (texto == null || texto.isBlank()) {
            return;
        }

        Mensagem m = new Mensagem();
        m.setRestauranteId(restauranteId);
        m.setTexto("[" + nomeAutor.toUpperCase() + "] " + texto);
        m.setDataHora(LocalDateTime.now());
        mensagemDAO.create(m);
    }

    @Override
    public void difundirMensagemGlobal(String texto, String nomeAutor) {
        if (texto == null || texto.isBlank()) {
            return;
        }

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
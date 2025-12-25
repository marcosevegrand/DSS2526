package dss2526.gestao;

import dss2526.data.contract.*;
import dss2526.domain.entity.*;
import dss2526.domain.enumeration.RoleTrabalhador;
import dss2526.domain.enumeration.Trabalho;
import dss2526.producao.IProducaoFacade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementação da Fachada de Gestão.
 * Atua como mediador entre a UI de Gestão e as camadas de dados/produção.
 */
public class GestaoFacade implements IGestaoFacade {

    private final PedidoDAO pedidoDAO;
    private final ProdutoDAO produtoDAO;
    private final RestauranteDAO restauranteDAO;
    private final FuncionarioDAO funcionarioDAO;
    private final EstacaoDAO estacaoDAO;
    private final IProducaoFacade producaoFacade;

    public GestaoFacade(PedidoDAO pedidoDAO, 
                        ProdutoDAO produtoDAO, 
                        RestauranteDAO restauranteDAO,
                        FuncionarioDAO funcionarioDAO,
                        EstacaoDAO estacaoDAO,
                        IProducaoFacade producaoFacade) {
        this.pedidoDAO = pedidoDAO;
        this.produtoDAO = produtoDAO;
        this.restauranteDAO = restauranteDAO;
        this.funcionarioDAO = funcionarioDAO;
        this.estacaoDAO = estacaoDAO;
        this.producaoFacade = producaoFacade;
    }

    // --- MÉTODOS DE CONFIGURAÇÃO (Sincronizados com GestaoUI) ---

    @Override
    public int registarRestaurante(String nome, String localizacao) {
        Restaurante r = new Restaurante();
        r.setNome(nome);
        r.setLocalizacao(localizacao);
        restauranteDAO.save(r); 
        return r.getId();
    }

    @Override
    public void adicionarEstacao(int restauranteId, Trabalho tipo) {
        Estacao e = new Estacao(restauranteId, tipo);
        estacaoDAO.save(e);
    }

    @Override
    public void contratarFuncionario(String nome, String user, String pass, RoleTrabalhador papel, int resId) {
        Funcionario f = new Funcionario(nome, user, pass, papel, resId);
        funcionarioDAO.save(f);
    }

    // MÉTODO QUE ESTAVA EM FALTA (Resolve o erro do compilador):
    @Override
    public void configurarNovoRestaurante(Restaurante r, List<Estacao> estacoes, List<Funcionario> funcionarios) {
        restauranteDAO.save(r);
        int id = r.getId();
        
        if (estacoes != null) {
            for (Estacao e : estacoes) { 
                e.setRestauranteId(id); 
                estacaoDAO.save(e); 
            }
        }
        if (funcionarios != null) {
            for (Funcionario f : funcionarios) { 
                f.setRestauranteId(id); 
                funcionarioDAO.save(f); 
            }
        }
    }

    // --- MÉTODOS DE COMUNICAÇÃO E STOCK ---

    @Override
    public void enviarMensagemProducao(int resId, String texto, boolean urgente) {
        Mensagem msg = new Mensagem();
        msg.setTexto(texto);
        msg.setUrgente(urgente);
        producaoFacade.difundirMensagem(msg, resId);
    }

    @Override
    public List<String> getAlertasStock(int restauranteId) {
        return producaoFacade.getAlertasStock(restauranteId);
    }

    @Override
    public void atualizarStockLocal(int ingredienteId, int restauranteId, float quantidade) {
        producaoFacade.atualizarStockLocal(ingredienteId, restauranteId, quantidade);
    }

    // --- MÉTODOS DE CONSULTA E RELATÓRIOS ---

    @Override
    public List<String> listarNomesRestaurantes() {
        return restauranteDAO.findAll().stream()
                .map(Restaurante::getNome)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal calcularFaturacao(LocalDate data, int idRestaurante) {
        return pedidoDAO.findByData(data).stream()
                .filter(p -> idRestaurante == 0 || p.getRestauranteId() == idRestaurante)
                .map(p -> BigDecimal.valueOf(p.calcularPrecoTotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<String, Integer> obterTopProdutosVendidos(int idRestaurante) {
        Map<Integer, Integer> contagem = new HashMap<>();
        pedidoDAO.findAll().stream()
                .filter(p -> idRestaurante == 0 || p.getRestauranteId() == idRestaurante)
                .forEach(p -> p.getLinhasPedido().forEach(lp -> {
                    int id = lp.getItem().getId();
                    contagem.put(id, contagem.getOrDefault(id, 0) + lp.getQuantidade());
                }));

        Map<String, Integer> resultado = new HashMap<>();
        contagem.forEach((id, qtd) -> {
            Produto prod = produtoDAO.findById(id);
            resultado.put(prod != null ? prod.getNome() : "Item #" + id, qtd);
        });
        return resultado;
    }

    @Override
    public double calcularTempoMedioEspera(LocalDate data, int idRestaurante) {
        return pedidoDAO.findByData(data).stream()
                .filter(p -> idRestaurante == 0 || p.getRestauranteId() == idRestaurante)
                .filter(p -> p.getHoraEntrega() != null)
                .mapToLong(Pedido::calcularTempoAtendimento)
                .average().orElse(0.0);
    }
}
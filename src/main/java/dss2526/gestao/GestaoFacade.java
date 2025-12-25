package dss2526.gestao;

import dss2526.data.contract.*;
import dss2526.domain.entity.*;
import dss2526.domain.enumeration.RoleTrabalhador;
import dss2526.domain.enumeration.Trabalho;
import dss2526.producao.IProducaoFacade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

    // --- ESTATÍSTICAS E RELATÓRIOS ---

    @Override
    public BigDecimal calcularFaturacao(LocalDate data, Integer idRestaurante) {
        return pedidoDAO.findByData(data).stream()
                .filter(p -> idRestaurante == null || p.getRestauranteId() == idRestaurante)
                .map(p -> BigDecimal.valueOf(p.calcularPrecoTotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<String, Integer> obterTopProdutosVendidos(Integer idRestaurante) {
        Map<Integer, Integer> contagem = new HashMap<>();
        List<Pedido> pedidos = pedidoDAO.findAll().stream()
                .filter(p -> idRestaurante == null || p.getRestauranteId() == idRestaurante)
                .collect(Collectors.toList());

        for (Pedido p : pedidos) {
            for (LinhaPedido lp : p.getLinhasPedido()) {
                int id = lp.getItem().getId();
                contagem.put(id, contagem.getOrDefault(id, 0) + lp.getQuantidade());
            }
        }

        Map<String, Integer> resultado = new HashMap<>();
        contagem.forEach((id, qtd) -> {
            Produto prod = produtoDAO.findById(id);
            resultado.put(prod != null ? prod.getNome() : "Item #" + id, qtd);
        });
        return resultado;
    }

    @Override
    public double calcularTempoMedioEspera(LocalDate data, Integer idRestaurante) {
        List<Pedido> concluidos = pedidoDAO.findByData(data).stream()
                .filter(p -> idRestaurante == null || p.getRestauranteId() == idRestaurante)
                .filter(p -> p.getHoraEntrega() != null)
                .collect(Collectors.toList());

        return concluidos.stream()
                .mapToLong(Pedido::calcularTempoAtendimento)
                .average().orElse(0.0);
    }

    @Override
    public List<String> verificarProdutosAbaixoDoStock(Integer idRestaurante) {
        // Delegamos à produção a verificação do stock local
        return producaoFacade.getAlertasStock(idRestaurante);
    }

    // --- CONFIGURAÇÃO DA CADEIA (CRIAÇÃO) ---

    @Override
    public int criarRestaurante(String nome, String localizacao) {
        Restaurante r = new Restaurante(nome, localizacao);
        return restauranteDAO.put(r); // Retorna o ID gerado
    }

    @Override
    public void adicionarEstacaotrabalho(int restauranteId, Trabalho tipo) {
        Estacao e = new Estacao(tipo);
        e.setRestauranteId(restauranteId);
        estacaoDAO.put(e);
    }

    @Override
    public void registarFuncionario(int restauranteId, String nome, String user, String pass, RoleTrabalhador papel) {
        Funcionario f = new Funcionario(nome, user, pass, papel);
        f.setRestauranteId(restauranteId);
        funcionarioDAO.put(f);
    }

    @Override
    public void configurarNovoRestaurante(Restaurante r, List<Estacao> estacoes, List<Funcionario> funcionarios) {
        int id = restauranteDAO.put(r);
        for (Estacao e : estacoes) { e.setRestauranteId(id); estacaoDAO.put(e); }
        for (Funcionario f : funcionarios) { f.setRestauranteId(id); funcionarioDAO.put(f); }
    }

    @Override
    public List<String> listarNomesRestaurantes() {
        return restauranteDAO.findAll().stream()
                .map(Restaurante::getNome)
                .collect(Collectors.toList());
    }

    // --- COMUNICAÇÃO ---

    @Override
    public void enviarMensagemIncentivo(String texto, Integer alvoRestauranteId) {
        Mensagem msg = new Mensagem(texto, false);
        producaoFacade.difundirMensagem(msg, alvoRestauranteId);
    }

    @Override
    public void reporStockIngrediente(int ingredienteId, int restauranteId, float quantidade) {
        producaoFacade.atualizarStockLocal(ingredienteId, restauranteId, quantidade);
    }
}
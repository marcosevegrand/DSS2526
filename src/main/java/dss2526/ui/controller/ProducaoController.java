package dss2526.ui.controller;

import dss2526.domain.entity.*;
import dss2526.service.producao.IProducaoFacade;
import dss2526.service.producao.ProducaoFacade;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProducaoController {
    private final IProducaoFacade facade;
    private int restauranteId = -1;
    private int estacaoId = -1;
    private int pedidoAtivoId = -1; // Rastreia o pedido selecionado para inspeção na caixa

    // Caches para mapeamento UI
    private List<Integer> cacheIdsRestaurantes = new ArrayList<>();
    private List<Integer> cacheIdsEstacoes = new ArrayList<>();
    private List<Integer> cacheIdsTarefasDisponiveis = new ArrayList<>();
    private List<Integer> cacheIdsTarefasEmExecucao = new ArrayList<>();
    private List<Integer> cacheIdsPedidosEntrega = new ArrayList<>();
    private List<Integer> cacheIdsLinhasPedidoAtivo = new ArrayList<>();

    public ProducaoController() {
        this.facade = ProducaoFacade.getInstance();
    }

    // --- Configuração ---
    public List<String> listarNomesRestaurantes() {
        List<Restaurante> lista = facade.listarRestaurantes();
        this.cacheIdsRestaurantes = lista.stream().map(Restaurante::getId).collect(Collectors.toList());
        return lista.stream().map(Restaurante::getNome).collect(Collectors.toList());
    }

    public void selecionarRestaurante(int index) {
        this.restauranteId = cacheIdsRestaurantes.get(index);
    }

    public List<String> listarNomesEstacoes() {
        List<Estacao> lista = facade.listarEstacoesDeRestaurante(restauranteId);
        this.cacheIdsEstacoes = lista.stream().map(Estacao::getId).collect(Collectors.toList());
        return lista.stream().map(e -> e.getNome()).collect(Collectors.toList());
    }

    public void selecionarEstacao(int index) {
        this.estacaoId = cacheIdsEstacoes.get(index);
    }

    public boolean ehEstacaoDeCaixa() {
        Estacao e = facade.obterEstacao(estacaoId);
        return e instanceof Estacao.Caixa;
    }

    // --- Fluxo Cozinha ---
    public List<String> getTarefasNovas() {
        List<Tarefa> tarefas = facade.listarTarefasDisponiveisParaIniciar(restauranteId, estacaoId);
        this.cacheIdsTarefasDisponiveis = tarefas.stream().map(Tarefa::getId).collect(Collectors.toList());
        return tarefas.stream().map(t -> {
            Produto p = facade.obterProduto(t.getProdutoId());
            Passo s = facade.obterPasso(t.getPassoId());
            return String.format("Pedido %d: %s -> %s", t.getPedidoId(), p.getNome(), s.getNome());
        }).collect(Collectors.toList());
    }

    public void iniciarTarefaSelecionada(int index) {
        facade.iniciarTarefa(cacheIdsTarefasDisponiveis.get(index));
    }

    public List<String> getTarefasEmCurso() {
        List<Tarefa> tarefas = facade.listarTarefasEmExecucaoNaEstacao(restauranteId, estacaoId);
        this.cacheIdsTarefasEmExecucao = tarefas.stream().map(Tarefa::getId).collect(Collectors.toList());
        return tarefas.stream().map(t -> {
            Produto p = facade.obterProduto(t.getProdutoId());
            return "Pedido " + t.getPedidoId() + ": " + p.getNome();
        }).collect(Collectors.toList());
    }

    public void concluirTarefaSelecionada(int index) {
        facade.concluirTarefa(cacheIdsTarefasEmExecucao.get(index));
    }

    // --- Fluxo Caixa e Falhas ---
    public List<String> getPedidosProntos() {
        List<Pedido> pedidos = facade.listarPedidosProntosParaEntrega(restauranteId);
        this.cacheIdsPedidosEntrega = pedidos.stream().map(Pedido::getId).collect(Collectors.toList());
        return pedidos.stream().map(p -> "Pedido #" + p.getId()).collect(Collectors.toList());
    }

    public List<String> getLinhasDePedido(int indexPedido) {
        // Armazena o ID do pedido selecionado para uso posterior no refazerLinha
        this.pedidoAtivoId = cacheIdsPedidosEntrega.get(indexPedido);
        
        Pedido p = facade.obterPedido(this.pedidoAtivoId);
        this.cacheIdsLinhasPedidoAtivo = p.getLinhas().stream().map(LinhaPedido::getId).collect(Collectors.toList());
        
        return p.getLinhas().stream().map(l -> {
            String nome = (l.getTipo() == dss2526.domain.enumeration.TipoItem.PRODUTO) ? 
                    facade.obterProduto(l.getItemId()).getNome() : facade.obterMenu(l.getItemId()).getNome();
            return nome + " (x" + l.getQuantidade() + ")";
        }).collect(Collectors.toList());
    }

    public void refazerLinha(int indexLinha) {
        if (this.pedidoAtivoId != -1) {
            int linhaId = cacheIdsLinhasPedidoAtivo.get(indexLinha);
            // Passa agora os dois identificadores conforme a assinatura da Facade
            facade.refazerLinhaPedido(this.pedidoAtivoId, linhaId);
        }
    }

    public void confirmarEntrega(int indexPedido) {
        facade.confirmarEntrega(cacheIdsPedidosEntrega.get(indexPedido));
    }

    // --- Outros ---
    public List<String> getMonitorGlobal() {
        return facade.consultarMonitorGlobal(restauranteId).stream()
                .map(p -> "Pedido " + p.getId() + " [" + p.getEstado() + "]")
                .collect(Collectors.toList());
    }
}
package dss2526.ui.controller;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.EstadoTarefa;
import dss2526.domain.enumeration.TipoItem;
import dss2526.service.producao.IProducaoFacade;
import dss2526.service.producao.ProducaoFacade;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProducaoController {
    
    private final IProducaoFacade facade;
    private Restaurante restauranteAtual;
    private Estacao estacaoAtual;
    
    // Caches para seleção da UI
    private List<Tarefa> cacheTarefasPendentes;
    private List<Tarefa> cacheTarefasEmCurso;
    private List<Ingrediente> cacheIngredientesTarefaAtual;

    // Caches para Visão Global (Drill-down)
    private List<Pedido> cachePedidosGlobal;
    private Pedido pedidoSelecionado;
    private List<LinhaPedido> cacheLinhasPedidoSelecionado;

    public ProducaoController() {
        this.facade = ProducaoFacade.getInstance();
    }

    // --- Setup de Sessão ---
    public List<String> getListaRestaurantes() {
        return facade.listarRestaurantes().stream()
                .map(r -> String.format("%-25s [ID: %d]", r.getNome(), r.getId()))
                .collect(Collectors.toList());
    }
    
    public void selecionarRestaurante(int index) { 
        this.restauranteAtual = facade.listarRestaurantes().get(index); 
    }
    
    public List<String> getListaEstacoes() {
        if (restauranteAtual == null) return new ArrayList<>();
        return facade.listarEstacoesDeRestaurante(restauranteAtual.getId()).stream()
                .map(e -> String.format("Estação %d - %s", e.getId(), e.getTrabalho()))
                .collect(Collectors.toList());
    }
    
    public void selecionarEstacao(int index) {
        if (restauranteAtual == null) return;
        List<Estacao> estacoes = facade.listarEstacoesDeRestaurante(restauranteAtual.getId());
        this.estacaoAtual = estacoes.get(index);
        // Removido 'inicioSessao' para não ocultar mensagens anteriores
    }

    // --- Tarefas Pendentes ---
    public List<String> getTarefasPendentesFormatadas() {
        if (restauranteAtual == null || estacaoAtual == null) return new ArrayList<>();
        this.cacheTarefasPendentes = facade.consultarTarefasOtimizadas(restauranteAtual.getId(), estacaoAtual.getId());
        
        List<String> output = new ArrayList<>();
        String formato = "PEDIDO #%d | %-30s | ⏳ Aguarda Início";
        for (Tarefa t : cacheTarefasPendentes) {
            String desc = resolverDescricaoTarefa(t);
            output.add(String.format(formato, t.getPedidoId(), desc));
        }
        return output;
    }
    
    public void iniciarTarefaPendente(int index) {
        if (cacheTarefasPendentes != null && index >= 0 && index < cacheTarefasPendentes.size()) {
            facade.iniciarTarefa(cacheTarefasPendentes.get(index).getId());
        }
    }
    
    // --- Tarefas Em Curso ---
    public List<String> getTarefasEmCursoFormatadas() {
        if (restauranteAtual == null || estacaoAtual == null) return new ArrayList<>();
        
        // Filtra localmente as tarefas para esta estação que estão EM_EXECUCAO ou ATRASADA
        List<Tarefa> todas = facade.listarTarefas();
        this.cacheTarefasEmCurso = todas.stream()
            .filter(t -> t.getEstado() == EstadoTarefa.EM_EXECUCAO || t.getEstado() == EstadoTarefa.ATRASADA)
            .filter(this::verificarContextoTarefa)
            .collect(Collectors.toList());

        List<String> output = new ArrayList<>();
        for (Tarefa t : cacheTarefasEmCurso) {
            String desc = resolverDescricaoTarefa(t);
            String estadoStr = (t.getEstado() == EstadoTarefa.ATRASADA) ? "⚠️ ATRASADA" : "▶️ Em execução";
            output.add(String.format("PEDIDO #%d | %-30s | %s", t.getPedidoId(), desc, estadoStr));
        }
        return output;
    }
    
    public void concluirTarefaEmCurso(int index) {
        if (cacheTarefasEmCurso != null && index >= 0 && index < cacheTarefasEmCurso.size()) {
            facade.concluirTarefa(cacheTarefasEmCurso.get(index).getId());
        }
    }
    
    public List<String> getIngredientesDaTarefaParaSelecao(int indexTarefa) {
        if (cacheTarefasEmCurso != null && indexTarefa >= 0 && indexTarefa < cacheTarefasEmCurso.size()) {
            Tarefa t = cacheTarefasEmCurso.get(indexTarefa);
            this.cacheIngredientesTarefaAtual = facade.listarIngredientesDaTarefa(t.getId());
            return cacheIngredientesTarefaAtual.stream()
                    .map(Ingrediente::getNome)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
    
    public void reportarAtrasoTarefaPorIndexIngrediente(int indexTarefa, int indexIngrediente) {
        if (cacheTarefasEmCurso != null && indexTarefa >= 0 && indexTarefa < cacheTarefasEmCurso.size()) {
            if (cacheIngredientesTarefaAtual != null && indexIngrediente >= 0 && indexIngrediente < cacheIngredientesTarefaAtual.size()) {
                Tarefa t = cacheTarefasEmCurso.get(indexTarefa);
                Ingrediente ing = cacheIngredientesTarefaAtual.get(indexIngrediente);
                facade.registarAtrasoPorFaltaIngrediente(t.getId(), ing.getId());
            }
        }
    }

    // --- Visão Global ---
    public List<String> getPedidosGlobaisFormatados() {
        if (restauranteAtual == null) return new ArrayList<>();
        this.cachePedidosGlobal = facade.consultarPedidosEmProducao(restauranteAtual.getId());
        
        List<String> output = new ArrayList<>();
        for (Pedido p : cachePedidosGlobal) {
            output.add(String.format("Pedido #%d | %s | Data: %s", 
                p.getId(), p.getEstado(), p.getDataHora().toLocalTime().toString().substring(0,5)));
        }
        return output;
    }
    
    public void selecionarPedidoGlobal(int index) {
        if (cachePedidosGlobal != null && index >= 0 && index < cachePedidosGlobal.size()) {
            this.pedidoSelecionado = cachePedidosGlobal.get(index);
            this.cacheLinhasPedidoSelecionado = this.pedidoSelecionado.getLinhas();
        }
    }
    
    public List<String> getLinhasPedidoSelecionadoFormatadas() {
        if (pedidoSelecionado == null) return new ArrayList<>();
        List<Tarefa> tarefasDoPedido = facade.consultarTarefasDoPedido(pedidoSelecionado.getId());
        List<String> output = new ArrayList<>();
        for (LinhaPedido lp : cacheLinhasPedidoSelecionado) {
            String nomeItem = resolverNomeItem(lp);
            List<Tarefa> tarefasDestaLinha = filtrarTarefasPorLinha(tarefasDoPedido, lp);
            long totalTarefas = tarefasDestaLinha.size();
            long concluidas = tarefasDestaLinha.stream().filter(t -> t.getEstado() == EstadoTarefa.CONCLUIDA).count();
            
            String status = (totalTarefas == 0) ? "Sem processamento" : 
                            (concluidas == totalTarefas) ? "✅ PRONTO" : 
                            String.format("🛠️ %d/%d passos", concluidas, totalTarefas);
                            
            output.add(String.format("%-25s | x%d | %s", nomeItem, lp.getQuantidade(), status));
        }
        return output;
    }
    
    public List<String> getDetalhesTarefasEmFaltaDaLinha(int indexLinha) {
        if (pedidoSelecionado == null || cacheLinhasPedidoSelecionado == null) return new ArrayList<>();
        LinhaPedido lp = cacheLinhasPedidoSelecionado.get(indexLinha);
        List<Tarefa> tarefasDoPedido = facade.consultarTarefasDoPedido(pedidoSelecionado.getId());
        List<Tarefa> tarefasDestaLinha = filtrarTarefasPorLinha(tarefasDoPedido, lp);
        return tarefasDestaLinha.stream()
                .filter(t -> t.getEstado() != EstadoTarefa.CONCLUIDA)
                .map(t -> {
                    String passo = resolverNomePasso(t.getPassoId());
                    String prod = facade.obterProduto(t.getProdutoId()).getNome();
                    String estado = (t.getEstado() == EstadoTarefa.PENDENTE) ? "Pendente" : 
                                    (t.getEstado() == EstadoTarefa.ATRASADA) ? "⚠️ ATRASADA" : "▶️ Em curso";
                    return String.format("[%s] %s - %s", estado, prod, passo);
                })
                .collect(Collectors.toList());
    }

    // --- Helpers e Mensagens ---
    public List<String> getNovasMensagens() {
        if (restauranteAtual == null) return new ArrayList<>();
        
        // Filtra mensagens das últimas 24 horas
        LocalDateTime limite = LocalDateTime.now().minusHours(24);
        
        return facade.consultarMensagens(restauranteAtual.getId()).stream()
                .filter(m -> m.getDataHora().isAfter(limite))
                .map(m -> String.format("[%s] %s", m.getDataHora().toLocalTime().toString().substring(0,5), m.getTexto()))
                .collect(Collectors.toList());
    }
    
    private List<Tarefa> filtrarTarefasPorLinha(List<Tarefa> todas, LinhaPedido lp) {
        List<Tarefa> filtradas = new ArrayList<>();
        if (lp.getTipo() == TipoItem.PRODUTO) {
            filtradas = todas.stream().filter(t -> t.getProdutoId() == lp.getItemId()).collect(Collectors.toList());
        } else {
            Menu m = facade.obterMenu(lp.getItemId());
            if (m != null) {
                List<Integer> idsProdutosMenu = m.getLinhas().stream().map(LinhaMenu::getProdutoId).collect(Collectors.toList());
                filtradas = todas.stream().filter(t -> idsProdutosMenu.contains(t.getProdutoId())).collect(Collectors.toList());
            }
        }
        return filtradas;
    }
    
    private boolean verificarContextoTarefa(Tarefa t) {
        Pedido p = facade.obterPedido(t.getPedidoId());
        if (p == null || p.getRestauranteId() != restauranteAtual.getId()) return false;
        Passo passo = facade.obterPasso(t.getPassoId());
        if (passo == null || passo.getTrabalho() != estacaoAtual.getTrabalho()) return false;
        return true;
    }
    
    private String resolverDescricaoTarefa(Tarefa t) {
        Produto p = facade.obterProduto(t.getProdutoId());
        String nomeProd = (p != null) ? p.getNome() : "Prod " + t.getProdutoId();
        Passo passo = facade.obterPasso(t.getPassoId());
        String nomePasso = (passo != null) ? passo.getNome() : "Passo " + t.getPassoId();
        
        String res = nomeProd + " - " + nomePasso;
        if (res.length() > 30) res = res.substring(0, 27) + "...";
        return res;
    }
    
    private String resolverNomeItem(LinhaPedido lp) {
        if (lp.getTipo() == TipoItem.PRODUTO) {
            Produto p = facade.obterProduto(lp.getItemId());
            return p != null ? "[P] " + p.getNome() : "Prod " + lp.getItemId();
        } else {
            Menu m = facade.obterMenu(lp.getItemId());
            return m != null ? "[M] " + m.getNome() : "Menu " + lp.getItemId();
        }
    }
    
    private String resolverNomePasso(int id) {
        Passo p = facade.obterPasso(id);
        return p != null ? p.getNome() : "Passo " + id;
    }
}
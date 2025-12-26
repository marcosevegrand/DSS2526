package dss2526.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.TipoItem;
import dss2526.service.venda.IVendaFacade;
import dss2526.service.venda.VendaFacade;

/**
 * Controller STATEFUL.
 * Controla o fluxo da interação e mantém o estado da sessão atual (Pedido em curso, Restaurante selecionado).
 */
public class VendaController {

    private final IVendaFacade vendaFacade;

    // --- Estado da Sessão ---
    private Restaurante restauranteSelecionado;
    private Pedido pedidoAtual;
    private List<String> alergenicosAtuais;
    
    // --- Caches para manter consistência com a UI ---
    // A UI seleciona por índice, então precisamos guardar o que mostrámos
    private List<Restaurante> cacheRestaurantes;
    private List<Produto> cacheProdutosDisponiveis;
    private List<Menu> cacheMenusDisponiveis;

    public VendaController() {
        this.vendaFacade = VendaFacade.getInstance();
        this.alergenicosAtuais = new ArrayList<>();
    }

    // 1. Seleção de Restaurante
    public List<String> getListaRestaurantes() {
        this.cacheRestaurantes = vendaFacade.listarRestaurantes();
        return cacheRestaurantes.stream()
                .map(r -> String.format("%-25s [ID: %d]", r.getNome(), r.getId())) 
                .collect(Collectors.toList());
    }

    public void selecionarRestaurante(int index) {
        if (cacheRestaurantes != null && index >= 0 && index < cacheRestaurantes.size()) {
            this.restauranteSelecionado = cacheRestaurantes.get(index);
        } else {
            throw new IllegalArgumentException("Restaurante inválido.");
        }
    }

    // 2. Início do Pedido
    public void iniciarPedido(boolean paraLevar, List<String> alergenicos) {
        if (restauranteSelecionado == null) throw new IllegalStateException("Restaurante não selecionado.");
        
        // Normalização dos alergénios (Trim + UpperCase) para garantir consistência
        this.alergenicosAtuais = alergenicos.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        // Delega a criação ao Facade
        this.pedidoAtual = vendaFacade.iniciarPedido(restauranteSelecionado, paraLevar);
        
        // Atualiza caches de itens disponíveis com base nas restrições
        atualizarItensDisponiveis();
    }

    private void atualizarItensDisponiveis() {
        this.cacheProdutosDisponiveis = vendaFacade.listarProdutosDisponiveis(restauranteSelecionado, alergenicosAtuais);
        this.cacheMenusDisponiveis = vendaFacade.listarMenusDisponiveis(restauranteSelecionado, alergenicosAtuais);
    }

    // 3. Listagem de Itens para UI
    public List<String> getItensDisponiveisLegiveis() {
        if (pedidoAtual == null) return new ArrayList<>();

        List<String> display = new ArrayList<>();
        String formato = "%-9s %-30s | %6.2f €"; // Alinhamento para 'tabela' visual
        
        // Prefixar para identificar na seleção
        for (Produto p : cacheProdutosDisponiveis) {
            display.add(String.format(formato, "[PRODUTO]", p.getNome(), p.getPreco()));
        }
        for (Menu m : cacheMenusDisponiveis) {
            display.add(String.format(formato, "[MENU]", m.getNome(), m.getPreco()));
        }
        return display;
    }

    // 4. Adicionar Item
    public void adicionarItemAoPedido(int indexGlobal, int quantidade) {
        if (pedidoAtual == null) return;

        LinhaPedido linha = new LinhaPedido();
        linha.setQuantidade(quantidade);

        // Lógica para mapear o índice global (Produto + Menu) para o objeto correto
        int numProdutos = cacheProdutosDisponiveis.size();

        if (indexGlobal < numProdutos) {
            // É um Produto
            Produto p = cacheProdutosDisponiveis.get(indexGlobal);
            linha.setItemId(p.getId());
            linha.setTipo(TipoItem.PRODUTO);
            linha.setPrecoUnitario(p.getPreco()); // Opcional, dependendo da entidade
        } else {
            // É um Menu
            int menuIndex = indexGlobal - numProdutos;
            if (menuIndex < cacheMenusDisponiveis.size()) {
                Menu m = cacheMenusDisponiveis.get(menuIndex);
                linha.setItemId(m.getId());
                linha.setTipo(TipoItem.MENU);
                linha.setPrecoUnitario(m.getPreco());
            } else {
                return; // Índice inválido
            }
        }

        // Delega ao Facade para processar a adição e atualizar o pedido
        this.pedidoAtual = vendaFacade.adicionarLinhaAoPedido(pedidoAtual, linha);
    }

    // 5. Gestão do Pedido
    public List<String> getResumoPedido() {
        if (pedidoAtual == null) return List.of("Nenhum pedido ativo.");
        
        List<String> resumo = new ArrayList<>();
        
        // Cabeçalho estilizado
        resumo.add("-------------------------------------------------------");
        resumo.add(String.format(" PEDIDO #%-4d | %s", pedidoAtual.getId(), restauranteSelecionado.getNome()));
        resumo.add("-------------------------------------------------------");
        
        double total = 0.0;
        int i = 0;
        String lineFormat = "%2d. %-32s  x%2d  | %6.2f €";

        for (LinhaPedido lp : pedidoAtual.getLinhas()) {
            String nomeItem = resolverNomeItem(lp);
            // Trunca nomes muito longos para não quebrar a formatação
            if (nomeItem.length() > 32) nomeItem = nomeItem.substring(0, 29) + "...";
            
            double subtotal = lp.getPrecoUnitario() * lp.getQuantidade(); 
            total += subtotal;
            
            // Alterado para (i + 1) para visualização amigável (1-based index)
            resumo.add(String.format(lineFormat, (i + 1), nomeItem, lp.getQuantidade(), subtotal));
            i++;
        }
        
        // Rodapé com totais
        resumo.add("-------------------------------------------------------");
        resumo.add(String.format(" TOTAL %35s | %6.2f €", "", total));
        resumo.add("-------------------------------------------------------");
        
        return resumo;
    }

    public void removerItemDoPedido(int indexLinha) {
        if (pedidoAtual != null) {
            this.pedidoAtual = vendaFacade.removerLinhaDoPedido(pedidoAtual, indexLinha);
        }
    }

    public String finalizarPedido() {
        if (pedidoAtual != null) {
            double minutos = vendaFacade.finalizarPedido(pedidoAtual);
            
            String msg = String.format("Pedido #%d Confirmado.\nTempo estimado de espera: %.0f minutos.", 
                    pedidoAtual.getId(), minutos);
            
            // Limpeza
            this.pedidoAtual = null;
            this.alergenicosAtuais = null;
            
            return msg;
        }
        return "Erro ao finalizar.";
    }

    // Helper
    private String resolverNomeItem(LinhaPedido lp) {
        // Como o controller tem acesso ao Facade (Base), pode buscar nomes se não estiverem na linha
        if (lp.getTipo() == TipoItem.PRODUTO) {
            Produto p = vendaFacade.obterProduto(lp.getItemId());
            return p != null ? p.getNome() : "Produto " + lp.getItemId();
        } else {
            Menu m = vendaFacade.obterMenu(lp.getItemId());
            return m != null ? m.getNome() : "Menu " + lp.getItemId();
        }
    }
}
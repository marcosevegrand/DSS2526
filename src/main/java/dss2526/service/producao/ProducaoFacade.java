package dss2526.service.producao;

import dss2526.data.contract.*;
import dss2526.domain.entity.*;
import dss2526.domain.enumeration.EstadoPedido;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ProducaoFacade implements IProducaoFacade {
    private final PedidoDAO pedidoDAO;
    private final TarefaDAO tarefaDAO;
    private final MensagemDAO mensagemDAO;
    private final EstacaoDAO estacaoDAO;
    private final ProdutoDAO produtoDAO;
    private final PassoDAO passoDAO;

    public ProducaoFacade(PedidoDAO pDAO, TarefaDAO tDAO, MensagemDAO mDAO, 
                          EstacaoDAO eDAO, ProdutoDAO prDAO, PassoDAO psDAO) {
        this.pedidoDAO = pDAO;
        this.tarefaDAO = tDAO;
        this.mensagemDAO = mDAO;
        this.estacaoDAO = eDAO;
        this.produtoDAO = prDAO;
        this.passoDAO = psDAO;
    }

    @Override
    public List<Tarefa> consultarTarefasEstacao(int restauranteId, int estacaoId) {
        Estacao estacao = estacaoDAO.findById(estacaoId);
        if (estacao == null) return new ArrayList<>();

        // 1. Gera tarefas para pedidos que acabaram de ser confirmados
        gerarTarefasParaPedidosNovos(restauranteId);

        // 2. Filtra tarefas pendentes cujo Passo corresponde ao Trabalho da Estação
        return tarefaDAO.findAll().stream()
                .filter(t -> !t.isConcluido())
                .filter(t -> {
                    // Verifica se o pedido pertence a este restaurante
                    Pedido p = pedidoDAO.findById(t.getPedidoId());
                    return p != null && p.getRestauranteId() == restauranteId;
                })
                .filter(t -> {
                    // Verifica se o trabalho do passo coincide com o da estação
                    Passo passo = passoDAO.findById(t.getPassoId());
                    return passo != null && passo.getTrabalho() == estacao.getTrabalho();
                })
                .sorted(Comparator.comparing(Tarefa::getDataCriacao))
                .collect(Collectors.toList());
    }

    private void gerarTarefasParaPedidosNovos(int restauranteId) {
        List<Pedido> confirmados = pedidoDAO.findAllByRestaurante(restauranteId).stream()
                .filter(p -> p.getEstado() == EstadoPedido.CONFIRMADO)
                .collect(Collectors.toList());

        for (Pedido p : confirmados) {
            boolean criouTarefa = false;
            for (LinhaPedido lp : p.getLinhas()) {
                Produto prod = produtoDAO.findById(lp.getItemId());
                if (prod != null) {
                    for (Integer passoId : prod.getPassoIds()) {
                        // Evita duplicados (idempotência)
                        if (!tarefaExistente(p.getId(), prod.getId(), passoId)) {
                            Tarefa t = new Tarefa();
                            t.setPedidoId(p.getId());
                            t.setProdutoId(prod.getId());
                            t.setPassoId(passoId);
                            t.setDataCriacao(LocalDateTime.now());
                            t.setConcluido(false);
                            tarefaDAO.create(t);
                            criouTarefa = true;
                        }
                    }
                }
            }
            if (criouTarefa) {
                p.setEstado(EstadoPedido.EM_PREPARACAO);
                pedidoDAO.update(p);
            }
        }
    }

    private boolean tarefaExistente(int pedId, int prodId, int passoId) {
        return tarefaDAO.findAllByPedido(pedId).stream()
                .anyMatch(t -> t.getProdutoId() == prodId && t.getPassoId() == passoId);
    }

    @Override
    public void concluirTarefa(int tarefaId) {
        Tarefa t = tarefaDAO.findById(tarefaId);
        if (t != null) {
            t.setConcluido(true);
            t.setDataConclusao(LocalDateTime.now());
            tarefaDAO.update(t);

            // Se todas as tarefas do pedido estiverem prontas, o pedido fica PRONTO
            List<Tarefa> total = tarefaDAO.findAllByPedido(t.getPedidoId());
            if (total.stream().allMatch(Tarefa::isConcluido)) {
                Pedido p = pedidoDAO.findById(t.getPedidoId());
                p.setEstado(EstadoPedido.PRONTO);
                pedidoDAO.update(p);
            }
        }
    }

    @Override
    public List<Mensagem> consultarMensagens(int restauranteId) {
        return mensagemDAO.findAllByRestaurante(restauranteId).stream()
                .sorted(Comparator.comparing(Mensagem::getDataHora).reversed())
                .collect(Collectors.toList());
    }

    @Override 
    public void difundirMensagem(int rId, String txt, boolean urg) {
        Mensagem m = new Mensagem();
        m.setRestauranteId(rId);
        m.setTexto((urg ? "[URGENTE] " : "") + txt);
        m.setDataHora(LocalDateTime.now());
        mensagemDAO.create(m); // Guarda para que a produção a veja na próxima consulta
    }

    @Override 
    public void atualizarStockLocal(int iId, int rId, float qtd) {
        // Aqui usarias um InventarioDAO ou IngredienteDAO 
        // para atualizar a tabela que cruza Restaurante e Ingrediente
        System.out.println("Stock do ingrediente " + iId + " no restaurante " + rId + " atualizado para " + qtd);
    }

    @Override 
    public void registarAlertaStock(int rId, int iId) {
        // Pode criar uma mensagem especial ou inserir numa tabela de alertas
        String alerta = "ALERTA: Falta de ingrediente ID " + iId;
        difundirMensagem(rId, alerta, true);
    }
}
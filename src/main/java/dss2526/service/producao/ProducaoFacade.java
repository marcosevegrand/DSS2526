package dss2526.service.producao;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.EstadoPedido;
import dss2526.service.base.BaseFacade;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ProducaoFacade extends BaseFacade implements IProducaoFacade {
    
    private static ProducaoFacade instance;

    private ProducaoFacade() {}

    public static synchronized ProducaoFacade getInstance() {
        if (instance == null) {
            instance = new ProducaoFacade();
        }
        return instance;
    }

    @Override
    public List<Tarefa> consultarTarefasEstacao(int restauranteId, int estacaoId) {
        Estacao estacao = estacaoDAO.findById(estacaoId);
        if (estacao == null) return new ArrayList<>();

        gerarTarefasParaPedidosNovos(restauranteId);

        return tarefaDAO.findAll().stream()
                .filter(t -> !t.isConcluido())
                .filter(t -> {
                    Pedido p = pedidoDAO.findById(t.getPedidoId());
                    return p != null && p.getRestauranteId() == restauranteId;
                })
                .filter(t -> {
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

            List<Tarefa> total = tarefaDAO.findAllByPedido(t.getPedidoId());
            if (total.stream().allMatch(Tarefa::isConcluido)) {
                Pedido p = pedidoDAO.findById(t.getPedidoId());
                if (p != null) {
                    p.setEstado(EstadoPedido.PRONTO);
                    pedidoDAO.update(p);
                }
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
        mensagemDAO.create(m);
    }

    @Override 
    public void atualizarStockLocal(int iId, int rId, float qtd) {
        System.out.println("Stock atualizado: Ingrediente " + iId + " -> " + qtd);
    }

    @Override 
    public void registarAlertaStock(int rId, int iId) {
        String alerta = "ALERTA: Falta de ingrediente ID " + iId;
        difundirMensagem(rId, alerta, true);
    }
}
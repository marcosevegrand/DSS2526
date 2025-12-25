package dss2526.producao;

import dss2526.data.contract.*;
import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;
import dss2526.domain.contract.Item;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class ProducaoFacade implements IProducaoFacade {
    private final TarefaDAO tarefaDAO;
    private final PedidoDAO pedidoDAO;
    private final IngredienteDAO ingredienteDAO;
    private final EstacaoDAO estacaoDAO;
    
    private final Map<Integer, Queue<Mensagem>> avisosPorRestaurante = new HashMap<>();
    private final Map<Integer, Integer> pedidosEmAtraso = new HashMap<>();

    public ProducaoFacade(TarefaDAO tarefaDAO, PedidoDAO pedidoDAO, 
                          IngredienteDAO ingredienteDAO, EstacaoDAO estacaoDAO) {
        this.tarefaDAO = tarefaDAO;
        this.pedidoDAO = pedidoDAO;
        this.ingredienteDAO = ingredienteDAO;
        this.estacaoDAO = estacaoDAO;
    }

    @Override
    public void registarNovoPedido(Pedido pedido) {
        for (LinhaPedido linha : pedido.getLinhasPedido()) {
            Item item = linha.getItem();
            if (item instanceof Produto) {
                gerarTarefas((Produto) item, pedido);
            } else if (item instanceof Menu) {
                Menu m = (Menu) item;
                for (LinhaMenu lm : m.getLinhasMenu()) {
                    gerarTarefas(lm.getProduto(), pedido);
                }
            }
        }
    }

    private void gerarTarefas(Produto p, Pedido ped) {
        if (p == null || p.getTarefas() == null) return;
        for (Tarefa modelo : p.getTarefas()) {
            Tarefa nova = new Tarefa();
            nova.setPedidoId(ped.getId());
            nova.setRestauranteId(ped.getRestauranteId());
            nova.setNome(modelo.getNome());
            nova.setTrabalho(modelo.getTrabalho());
            nova.setConcluida(false);
            tarefaDAO.save(nova);
        }
    }

    @Override
    public void difundirMensagem(Mensagem msg, int restauranteId) {
        if (restauranteId == 0) {
            avisosPorRestaurante.keySet().forEach(id -> avisosPorRestaurante.get(id).add(msg));
        } else {
            avisosPorRestaurante.computeIfAbsent(restauranteId, k -> new ConcurrentLinkedQueue<>()).add(msg);
        }
    }

    @Override
    public void atualizarStockLocal(int ingredienteId, int restauranteId, float quantidade) {
        reportarReabastecimento(ingredienteId, restauranteId);
    }

    @Override
    public List<String> getAlertasStock(int restauranteId) {
        return new ArrayList<>(); 
    }

    @Override
    public List<Tarefa> obterTarefas(int restauranteId, Trabalho tipoEstacao) {
        return tarefaDAO.findAll().stream()
                .filter(t -> t.getRestauranteId() == restauranteId)
                .filter(t -> t.getTrabalho() == tipoEstacao)
                .filter(t -> !t.isConcluida())
                .sorted((t1, t2) -> {
                    boolean b1 = pedidosEmAtraso.containsKey(t1.getPedidoId());
                    boolean b2 = pedidosEmAtraso.containsKey(t2.getPedidoId());
                    if (b1 && !b2) return -1;
                    if (!b1 && b2) return 1;
                    return t1.getDataCriacao().compareTo(t2.getDataCriacao());
                })
                .collect(Collectors.toList());
    }

    @Override
    public void iniciarTarefa(int idTarefa) {
        Tarefa t = tarefaDAO.findById(idTarefa);
        if (t != null) {
            Pedido p = pedidoDAO.findById(t.getPedidoId());
            if (p != null) {
                p.setEstado(EstadoPedido.EM_PREPARACAO);
                pedidoDAO.update(p);
            }
        }
    }

    @Override
    public void concluirTarefa(int idTarefa) {
        Tarefa t = tarefaDAO.findById(idTarefa);
        if (t != null) {
            t.setConcluida(true);
            t.setDataFim(java.time.LocalDateTime.now());
            tarefaDAO.update(t);
            verificarPedidoConcluido(t.getPedidoId());
        }
    }

    private void verificarPedidoConcluido(int pedidoId) {
        List<Tarefa> tarefas = tarefaDAO.findAll().stream()
                .filter(t -> t.getPedidoId() == pedidoId)
                .collect(Collectors.toList());

        if (!tarefas.isEmpty() && tarefas.stream().allMatch(Tarefa::isConcluida)) {
            Pedido p = pedidoDAO.findById(pedidoId);
            if (p != null) {
                p.setEstado(EstadoPedido.PRONTO);
                p.setHoraEntrega(java.time.LocalDateTime.now());
                pedidoDAO.update(p);
            }
        }
    }

    @Override
    public void reportarFaltaIngrediente(int idTarefa, int idIngrediente, int restauranteId) {
        Tarefa t = tarefaDAO.findById(idTarefa);
        if (t != null) {
            pedidosEmAtraso.put(t.getPedidoId(), idIngrediente);
            Mensagem m = new Mensagem();
            m.setTexto("FALTA STOCK: Ingrediente #" + idIngrediente + " para " + t.getNome());
            m.setUrgente(true);
            difundirMensagem(m, restauranteId);
        }
    }

    private void reportarReabastecimento(int idIngrediente, int restauranteId) {
        pedidosEmAtraso.entrySet().removeIf(entry -> {
            Pedido p = pedidoDAO.findById(entry.getKey());
            return p != null && p.getRestauranteId() == restauranteId && entry.getValue() == idIngrediente;
        });
    }

    @Override
    public List<Mensagem> lerAvisosPendentes(int restauranteId) {
        Queue<Mensagem> fila = avisosPorRestaurante.get(restauranteId);
        List<Mensagem> lista = new ArrayList<>();
        if (fila != null) while (!fila.isEmpty()) lista.add(fila.poll());
        return lista;
    }

    @Override
    public List<Estacao> listarEstacoesPorRestaurante(int restauranteId) {
        return estacaoDAO.findAll().stream()
                .filter(e -> e.getRestauranteId() == restauranteId)
                .collect(Collectors.toList());
    }
}
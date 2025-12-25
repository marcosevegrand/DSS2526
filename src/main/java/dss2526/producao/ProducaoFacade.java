package dss2526.producao;

import dss2526.data.contract.*;
import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;
import dss2526.domain.contract.Item;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Fachada de Produção corrigida para lidar com a hierarquia:
 * Menu -> LinhaMenu -> Produto -> Tarefas
 */
public class ProducaoFacade implements IProducaoFacade {
    private final TarefaDAO tarefaDAO;
    private final PedidoDAO pedidoDAO;
    private final IngredienteDAO ingredienteDAO;
    private final EstacaoDAO estacaoDAO;
    
    // Controlo de avisos e atrasos em memória
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
                processarProduto((Produto) item, pedido);
            } 
            else if (item instanceof Menu) {
                Menu menu = (Menu) item;
                // Navega pela lista de LinhaMenu que o teu Menu possui
                if (menu.getLinhasMenu() != null) {
                    for (LinhaMenu lm : menu.getLinhasMenu()) {
                        // LinhaMenu -> Produto
                        processarProduto(lm.getProduto(), pedido);
                    }
                }
            }
        }
    }

    private void processarProduto(Produto prod, Pedido pedido) {
        // Acede à lista de tarefas (moldes) definida no Produto
        if (prod != null && prod.getTarefas() != null) {
            for (Tarefa modelo : prod.getTarefas()) {
                Tarefa nova = new Tarefa();
                nova.setPedidoId(pedido.getId());
                nova.setRestauranteId(pedido.getRestauranteId());
                nova.setNome(modelo.getNome());
                // Garante que a classe Tarefa tem o método getTrabalho()
                nova.setTrabalho(modelo.getTrabalho()); 
                nova.setConcluida(false);
                // Persiste a tarefa individual para este pedido
                tarefaDAO.save(nova);
            }
        }
    }

    @Override
    public List<Tarefa> obterTarefas(int restauranteId, Trabalho tipoEstacao) {
        return tarefaDAO.findAll().stream()
                .filter(t -> t.getRestauranteId() == restauranteId)
                .filter(t -> t.getTrabalho() == tipoEstacao)
                .filter(t -> !t.isConcluida())
                .sorted((t1, t2) -> {
                    // Prioriza pedidos que estão em atraso por falta de ingredientes
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
            if (p != null && p.getEstado() == EstadoPedido.CONFIRMADO) {
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
            tarefaDAO.update(t);
            verificarPedidoConcluido(t.getPedidoId());
        }
    }

    private void verificarPedidoConcluido(int pedidoId) {
        // Vai buscar todas as tarefas reais deste pedido
        List<Tarefa> tarefas = tarefaDAO.findAll().stream()
                .filter(t -> t.getPedidoId() == pedidoId)
                .collect(Collectors.toList());

        // Se todas as tarefas do pedido estiverem concluídas, o pedido está pronto
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
            String texto = "ALERTA: Falta de stock do ingrediente #" + idIngrediente + " na tarefa " + t.getNome();
            receberMensagemGerencia(new Mensagem(texto, true), restauranteId);
        }
    }

    @Override
    public void reportarReabastecimento(int idIngrediente, int restauranteId) {
        // Remove dos atrasos os pedidos que dependiam deste ingrediente
        pedidosEmAtraso.entrySet().removeIf(entry -> {
            Pedido p = pedidoDAO.findById(entry.getKey());
            return p != null && p.getRestauranteId() == restauranteId && entry.getValue() == idIngrediente;
        });
    }

    @Override
    public void receberMensagemGerencia(Mensagem msg, int restauranteId) {
        avisosPorRestaurante.computeIfAbsent(restauranteId, k -> new ConcurrentLinkedQueue<>()).add(msg);
    }

    @Override
    public List<Mensagem> lerAvisosPendentes(int restauranteId) {
        Queue<Mensagem> fila = avisosPorRestaurante.get(restauranteId);
        List<Mensagem> lista = new ArrayList<>();
        if (fila != null) {
            while (!fila.isEmpty()) {
                lista.add(fila.poll());
            }
        }
        return lista;
    }

    @Override
    public List<Estacao> listarEstacoesPorRestaurante(int restauranteId) {
        return estacaoDAO.findAll().stream()
                .filter(e -> e.getRestauranteId() == restauranteId)
                .collect(Collectors.toList());
    }    
}
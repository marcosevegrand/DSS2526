package dss2526.producao;

import dss2526.data.contract.*;
import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProducaoFacade implements IProducaoFacade {
    private final PassoDAO tarefaDAO;
    private final PedidoDAO pedidoDAO;
    private final IngredienteDAO ingredienteDAO;
    
    // Queue em memória para avisos rápidos do gerente
    private final Queue<Mensagem> avisosGerencia = new ConcurrentLinkedQueue<>();
    
    // Controlo de bloqueios: PedidoID -> IngredienteID que falta
    private final Map<Integer, Integer> pedidosEmAtraso = new HashMap<>();

    public ProducaoFacade(PassoDAO tarefaDAO, PedidoDAO pedidoDAO, IngredienteDAO ingredienteDAO) {
        this.tarefaDAO = tarefaDAO;
        this.pedidoDAO = pedidoDAO;
        this.ingredienteDAO = ingredienteDAO;
    }

    @Override
    public void registarNovoPedido(Pedido pedido) {
        for (LinhaPedido linha : pedido.getLinhasPedido()) {
            if (linha.getItem() instanceof Produto) {
                Produto prod = (Produto) linha.getItem(); 

                for (PassoProducao passo : prod.getPassos()) {
                    Passo t = new Passo();
                    t.setPedido(pedido);
                    t.setProduto(prod);
                    t.setPasso(passo);
                    t.setEstacao(passo.getEstacao());
                    tarefaDAO.save(t);
                }
            }
        }
    }

    @Override
    public void iniciarTarefa(int idTarefa) {
        Passo t = tarefaDAO.get(idTarefa);
        if (t != null) {
            Pedido p = t.getPedido();
            // Sincronização: Se é a primeira tarefa, avisa que o pedido começou
            if (p.getEstado() == EstadoPedido.CONFIRMADO) {
                p.setEstado(EstadoPedido.EM_PREPARACAO);
                pedidoDAO.put(p.getId(), p);
            }
        }
    }

    @Override
    public void reportarFaltaIngrediente(int idTarefa, int idIngrediente) {
        Passo t = tarefaDAO.get(idTarefa);
        if (t == null) return;

        int pId = t.getPedido().getId();
        pedidosEmAtraso.put(pId, idIngrediente);
        
        Ingrediente ing = ingredienteDAO.get(idIngrediente);
        String nomeIng = (ing != null) ? ing.getNome() : "Ingrediente #" + idIngrediente;
        
        // Notifica o gerente e as outras estações
        String aviso = String.format("PEDIDO #%d PARADO: Falta de %s na estação %s", 
                                     pId, nomeIng, t.getEstacao());
        receberMensagemGerencia(new Mensagem(aviso, true));
    }

    @Override
    public List<Passo> obterTarefasPorEstacao(Estacao estacao) {
        return tarefaDAO.findByEstacao(estacao).stream()
                .filter(t -> !t.getConcluida())
                .sorted((t1, t2) -> {
                    // Ordenação inteligente: Pedidos bloqueados vão para o fim da fila
                    boolean b1 = pedidosEmAtraso.containsKey(t1.getPedido().getId());
                    boolean b2 = pedidosEmAtraso.containsKey(t2.getPedido().getId());
                    if (b1 && !b2) return 1;
                    if (!b1 && b2) return -1;
                    return t1.getDataCriacao().compareTo(t2.getDataCriacao());
                })
                .toList();
    }

    @Override
    public void reportarReabastecimento(int idIngrediente) {
        // Remove todos os pedidos que estavam bloqueados por este ingrediente
        pedidosEmAtraso.entrySet().removeIf(entry -> entry.getValue() == idIngrediente);
    }

    @Override
    public void concluirTarefa(int idTarefa) {
        Passo t = tarefaDAO.get(idTarefa);
        if (t != null) {
            t.setConcluida(true);
            tarefaDAO.put(idTarefa, t);
            verificarPedidoConcluido(t.getPedido().getId());
        }
    }

    private void verificarPedidoConcluido(int pedidoId) {
        // Busca todas as tarefas deste pedido
        List<Passo> tarefas = tarefaDAO.values().stream()
                .filter(t -> t.getPedido().getId() == pedidoId)
                .toList();

        if (tarefas.stream().allMatch(Passo::getConcluida)) {
            Pedido p = pedidoDAO.get(pedidoId);
            p.setEstado(EstadoPedido.PRONTO); // Pronto para a Estação de Entrega
            pedidoDAO.put(pedidoId, p);
        }
    }

    @Override
    public void receberMensagemGerencia(Mensagem msg) {
        avisosGerencia.add(msg);
    }

    @Override
    public Mensagem lerProximoAviso() {
        return avisosGerencia.poll();
    }
}
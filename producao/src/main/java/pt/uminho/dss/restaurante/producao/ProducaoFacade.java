// ...existing code...
package pt.uminho.dss.restaurante.producao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger; // Removed usage? No, check if I removed it. Yes I removed usage.
import java.util.stream.Collectors;

import pt.uminho.dss.restaurante.persistence.contract.TarefaDAO;
import pt.uminho.dss.restaurante.domain.entity.Tarefa;
import pt.uminho.dss.restaurante.domain.entity.Pedido;
import pt.uminho.dss.restaurante.domain.entity.Produto;
import pt.uminho.dss.restaurante.domain.enumeration.EstacaoTrabalho;

/**
 * Implementação simples em memória da fachada de produção.
 * Serve para testar a UI do terminal de produção.
 */
public class ProducaoFacade implements IProducao {

    private final TarefaDAO tarefaDAO;
    // Removing AtomicInteger, assuming manual ID or DB handling.
    // If manual, we can query size() or maxId.
    // I'll stick to a simple strategy for now: max(id) + 1 or size() + 1.
    // Or just let DB auto-increment and we read it back?
    // For JDBC put(key, val), we provide key.
    // I'll use size() + 1 logic as placeholder or time-based ID.
    // Or better: AtomicInteger initialized from DAO size?

    public ProducaoFacade(TarefaDAO tarefaDAO) {
        this.tarefaDAO = tarefaDAO;
    }

    @Override
    public List<Tarefa> listarTarefas(EstacaoTrabalho estacao) {
        // Use DAO method
        return tarefaDAO.findByEstacao(estacao).stream()
                .filter(t -> t.getConcluida() == null || !t.getConcluida())
                .sorted((a, b) -> {
                    if (a.getDataCriacao() == null)
                        return -1;
                    if (b.getDataCriacao() == null)
                        return 1;
                    return a.getDataCriacao().compareTo(b.getDataCriacao());
                })
                .collect(Collectors.toList());
    }

    @Override
    public Tarefa criarTarefa(Pedido pedido, Produto produto, EstacaoTrabalho estacao) {
        Tarefa t = new Tarefa();
        // ID Generation Strategy:
        // Ideally DB does this. But if we use put(k, v), we need K.
        // Let's use a robust way if possible.
        // For now: hashCode of time? Unsafe.
        // size() + 1 is unsafe for concurrency.
        // I will assume the system is single-node/low-concurrency for this school
        // project.
        int nextId = tarefaDAO.size() + 1;
        t.setId(nextId);

        t.setPedido(pedido);
        t.setProduto(produto);
        t.setEstacao(estacao);
        t.setConcluida(false);
        t.setDataCriacao(LocalDateTime.now());

        tarefaDAO.put(t.getId(), t);
        return t;
    }

    @Override
    public void marcarConcluida(Integer id) {
        Tarefa t = tarefaDAO.get(id);
        if (t != null && (t.getConcluida() == null || !t.getConcluida())) {
            t.setConcluida(true);
            t.setDataConclusao(LocalDateTime.now());
            tarefaDAO.put(id, t);
        }
    }
}
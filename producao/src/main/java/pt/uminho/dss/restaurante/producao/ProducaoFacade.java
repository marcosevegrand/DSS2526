// ...existing code...
package pt.uminho.dss.restaurante.producao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import pt.uminho.dss.restaurante.domain.entity.Tarefa;
import pt.uminho.dss.restaurante.domain.entity.Pedido;
import pt.uminho.dss.restaurante.domain.entity.Produto;
import pt.uminho.dss.restaurante.domain.enumeration.EstacaoTrabalho;

/**
 * Implementação simples em memória da fachada de produção.
 * Serve para testar a UI do terminal de produção.
 */
public class ProducaoFacade implements IProducao {

    private final Map<Long, Tarefa> tarefas = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public List<Tarefa> listarTarefas(EstacaoTrabalho estacao) {
        return tarefas.values().stream()
            .filter(t -> t.getEstacao() == estacao && (t.getConcluida() == null || !t.getConcluida()))
            .sorted((a, b) -> a.getDataCriacao().compareTo(b.getDataCriacao()))
            .collect(Collectors.toList());
    }

    @Override
    public Tarefa criarTarefa(Pedido pedido, Produto produto, EstacaoTrabalho estacao) {
        Tarefa t = new Tarefa();
        t.setId(nextId.getAndIncrement());
        t.setPedido(pedido);
        t.setProduto(produto);
        t.setEstacao(estacao);
        t.setConcluida(false);
        t.setDataCriacao(LocalDateTime.now());
        tarefas.put(t.getId(), t);
        return t;
    }

    @Override
    public void marcarConcluida(Long id) {
        Tarefa t = tarefas.get(id);
        if (t != null && (t.getConcluida() == null || !t.getConcluida())) {
            t.setConcluida(true);
            t.setDataConclusao(LocalDateTime.now());
            tarefas.put(id, t);
        }
    }
}
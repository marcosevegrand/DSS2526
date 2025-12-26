package dss2526.service.producao;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.EstadoPedido;
import dss2526.domain.enumeration.EstadoTarefa;
import dss2526.domain.enumeration.Trabalho;
import dss2526.service.base.BaseFacade;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    public List<Tarefa> consultarTarefasOtimizadas(int restauranteId, int estacaoId) {
        // 1. Garante que novos pedidos têm tarefas geradas
        gerarTarefasParaPedidosNovos(restauranteId);
        
        Estacao estacao = estacaoDAO.findById(estacaoId);
        if (estacao == null) return new ArrayList<>();
        Trabalho trabalhoEstacao = estacao.getTrabalho();

        // 2. Obtém pedidos ativos (exclui os já entregues ou prontos, focando no fluxo de cozinha)
        // Nota: A caixa pode ver pedidos PRONTO para os entregar.
        List<Pedido> pedidosAtivos = pedidoDAO.findAllByRestaurante(restauranteId).stream()
                .filter(p -> p.getEstado() != EstadoPedido.ENTREGUE) 
                .collect(Collectors.toList());

        List<Tarefa> tarefasParaMostrar = new ArrayList<>();
        
        // 3. Algoritmo de Sincronização Temporal
        for (Pedido p : pedidosAtivos) {
            List<Tarefa> tarefasDoPedido = tarefaDAO.findAllByPedido(p.getId());
            
            List<Tarefa> tarefasPendentes = tarefasDoPedido.stream()
                    .filter(t -> t.getEstado() == EstadoTarefa.PENDENTE)
                    .collect(Collectors.toList());
            
            // Consideramos "iniciadas" quaisquer tarefas que já não estejam pendentes
            List<Tarefa> tarefasIniciadas = tarefasDoPedido.stream()
                    .filter(t -> t.getEstado() != EstadoTarefa.PENDENTE && t.getDataInicio() != null)
                    .collect(Collectors.toList());
            
            long maxDuracaoSegundos = calcularMaiorDuracao(tarefasDoPedido);

            if (tarefasIniciadas.isEmpty()) {
                // CENÁRIO A: Nada começou (Caminho Crítico)
                // Mostra apenas as tarefas mais longas
                for (Tarefa t : tarefasPendentes) {
                    if (verificarTrabalhoTarefa(t, trabalhoEstacao)) {
                        long duracaoTarefa = obterDuracaoTarefa(t);
                        // Tolerância de 10% ou exato? Vamos assumir >= max
                        if (duracaoTarefa >= maxDuracaoSegundos) {
                            tarefasParaMostrar.add(t);
                        }
                    }
                }
            } else {
                // CENÁRIO B: Já há tarefas a decorrer (Just-in-Time)
                LocalDateTime t0 = tarefasIniciadas.stream()
                        .map(Tarefa::getDataInicio)
                        .min(LocalDateTime::compareTo)
                        .orElse(LocalDateTime.now());
                
                long segundosDecorridos = ChronoUnit.SECONDS.between(t0, LocalDateTime.now());
                
                for (Tarefa t : tarefasPendentes) {
                    if (verificarTrabalhoTarefa(t, trabalhoEstacao)) {
                        long duracaoTarefa = obterDuracaoTarefa(t);
                        long folgaNecessaria = maxDuracaoSegundos - duracaoTarefa;
                        
                        // Se já passou tempo suficiente para começar esta tarefa curta
                        if (segundosDecorridos >= folgaNecessaria) {
                            tarefasParaMostrar.add(t);
                        }
                    }
                }
            }
        }
        
        // Ordena por antiguidade do pedido
        tarefasParaMostrar.sort(Comparator.comparingInt(Tarefa::getPedidoId));
        return tarefasParaMostrar;
    }

    @Override
    public void iniciarTarefa(int tarefaId) {
        Tarefa t = tarefaDAO.findById(tarefaId);
        if (t != null && t.getEstado() == EstadoTarefa.PENDENTE) {
            t.setEstado(EstadoTarefa.EM_EXECUCAO);
            t.setDataInicio(LocalDateTime.now());
            tarefaDAO.update(t);
        }
    }

    @Override
    public void concluirTarefa(int tarefaId) {
        Tarefa t = tarefaDAO.findById(tarefaId);
        if (t != null) {
            t.setEstado(EstadoTarefa.CONCLUIDA);
            t.setDataConclusao(LocalDateTime.now());
            if (t.getDataInicio() == null) t.setDataInicio(LocalDateTime.now()); // Fallback
            tarefaDAO.update(t);

            // Regra Especial: Se for tarefa de CAIXA, o pedido é entregue
            Passo p = passoDAO.findById(t.getPassoId());
            if (p != null && p.getTrabalho() == Trabalho.CAIXA) {
                finalizarPedidoComoEntregue(t.getPedidoId());
            } else {
                verificarConclusaoPedido(t.getPedidoId());
            }
        }
    }

    private void finalizarPedidoComoEntregue(int pedidoId) {
        Pedido p = pedidoDAO.findById(pedidoId);
        if (p != null) {
            p.setEstado(EstadoPedido.ENTREGUE);
            pedidoDAO.update(p);
        }
    }

    private void verificarConclusaoPedido(int pedidoId) {
        List<Tarefa> total = tarefaDAO.findAllByPedido(pedidoId);
        boolean tudoConcluido = total.stream().allMatch(t -> t.getEstado() == EstadoTarefa.CONCLUIDA);
        
        if (tudoConcluido && !total.isEmpty()) {
            Pedido p = pedidoDAO.findById(pedidoId);
            // Só avança para PRONTO se ainda não estiver ENTREGUE
            if (p != null && p.getEstado() != EstadoPedido.ENTREGUE) {
                p.setEstado(EstadoPedido.PRONTO);
                pedidoDAO.update(p);
            }
        }
    }

    @Override
    public void registarAtrasoPorFaltaIngrediente(int tarefaId, int ingredienteId) {
        Tarefa t = tarefaDAO.findById(tarefaId);
        if (t != null) {
            t.setEstado(EstadoTarefa.ATRASADA);
            tarefaDAO.update(t);
            
            Ingrediente ing = ingredienteDAO.findById(ingredienteId);
            String nomeIng = (ing != null) ? ing.getNome() : "ID " + ingredienteId;
            
            Pedido p = pedidoDAO.findById(t.getPedidoId());
            int restauranteId = (p != null) ? p.getRestauranteId() : -1;
            
            if (restauranteId != -1) {
                String msg = String.format("Tarefa #%d PARADA (Pedido #%d) - Falta %s", 
                        t.getId(), t.getPedidoId(), nomeIng);
                difundirMensagem(restauranteId, msg, true);
            }
        }
    }

    @Override
    public List<Ingrediente> listarIngredientesDaTarefa(int tarefaId) {
        Tarefa t = tarefaDAO.findById(tarefaId);
        if (t == null) return List.of();
        Passo p = passoDAO.findById(t.getPassoId());
        if (p == null) return List.of();
        return p.getIngredienteIds().stream()
                .map(ingredienteDAO::findById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Pedido> consultarPedidosEmProducao(int restauranteId) {
        gerarTarefasParaPedidosNovos(restauranteId);
        return pedidoDAO.findAllByRestaurante(restauranteId).stream()
                .filter(p -> p.getEstado() != EstadoPedido.ENTREGUE)
                .sorted(Comparator.comparingInt(Pedido::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Tarefa> consultarTarefasDoPedido(int pedidoId) {
        return tarefaDAO.findAllByPedido(pedidoId);
    }

    // --- Helpers de Lógica Interna ---

    private void gerarTarefasParaPedidosNovos(int restauranteId) {
        List<Pedido> confirmados = pedidoDAO.findAllByRestaurante(restauranteId).stream()
                .filter(p -> p.getEstado() == EstadoPedido.CONFIRMADO)
                .collect(Collectors.toList());

        for (Pedido p : confirmados) {
            boolean criouAlguma = false;
            for (LinhaPedido lp : p.getLinhas()) {
                Produto prod = produtoDAO.findById(lp.getItemId());
                if (prod != null) {
                    criouAlguma |= criarTarefasProduto(p.getId(), prod, lp.getQuantidade());
                } else {
                    Menu menu = menuDAO.findById(lp.getItemId());
                    if (menu != null) {
                        for (LinhaMenu lm : menu.getLinhas()) {
                            Produto pMenu = produtoDAO.findById(lm.getProdutoId());
                            if (pMenu != null) {
                                criouAlguma |= criarTarefasProduto(p.getId(), pMenu, lm.getQuantidade() * lp.getQuantidade());
                            }
                        }
                    }
                }
            }
            if (criouAlguma) {
                p.setEstado(EstadoPedido.EM_PREPARACAO);
                pedidoDAO.update(p);
            }
        }
    }

    private boolean criarTarefasProduto(int pedidoId, Produto prod, int quantidade) {
        boolean criou = false;
        for (int i = 0; i < quantidade; i++) {
            for (Integer passoId : prod.getPassoIds()) {
                Tarefa t = new Tarefa();
                t.setPedidoId(pedidoId);
                t.setProdutoId(prod.getId());
                t.setPassoId(passoId);
                t.setDataCriacao(LocalDateTime.now());
                t.setEstado(EstadoTarefa.PENDENTE);
                tarefaDAO.create(t);
                criou = true;
            }
        }
        return criou;
    }

    private long calcularMaiorDuracao(List<Tarefa> tarefas) {
        long max = 0;
        for (Tarefa t : tarefas) {
            long dur = obterDuracaoTarefa(t);
            if (dur > max) max = dur;
        }
        return max;
    }

    private long obterDuracaoTarefa(Tarefa t) {
        Passo p = passoDAO.findById(t.getPassoId());
        return (p != null && p.getDuracao() != null) ? p.getDuracao().toSeconds() : 0;
    }

    private boolean verificarTrabalhoTarefa(Tarefa t, Trabalho trabalhoEstacao) {
        Passo p = passoDAO.findById(t.getPassoId());
        return p != null && p.getTrabalho() == trabalhoEstacao;
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
        m.setTexto(txt); // Removido "[URGENTE]" redundante
        m.setDataHora(LocalDateTime.now());
        mensagemDAO.create(m);
    }
}
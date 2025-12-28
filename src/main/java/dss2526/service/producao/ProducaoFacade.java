package dss2526.service.producao;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import dss2526.domain.entity.Estacao;
import dss2526.domain.entity.Ingrediente;
import dss2526.domain.entity.LinhaMenu;
import dss2526.domain.entity.LinhaPedido;
import dss2526.domain.entity.Mensagem;
import dss2526.domain.entity.Menu;
import dss2526.domain.entity.Passo;
import dss2526.domain.entity.Pedido;
import dss2526.domain.entity.Produto;
import dss2526.domain.entity.Tarefa;
import dss2526.domain.enumeration.EstadoPedido;
import dss2526.domain.enumeration.EstadoTarefa;
import dss2526.domain.enumeration.TipoItem;
import dss2526.domain.enumeration.Trabalho;
import dss2526.service.base.BaseFacade;

public class ProducaoFacade extends BaseFacade implements IProducaoFacade {
    private static ProducaoFacade instance;
    private ProducaoFacade() {}
    public static synchronized ProducaoFacade getInstance() {
        if (instance == null) instance = new ProducaoFacade();
        return instance;
    }

    @Override
    public List<Tarefa> consultarTarefasOtimizadas(int restauranteId, int estacaoId) {
        gerarTarefasParaPedidosNovos(restauranteId);
        Estacao estacao = estacaoDAO.findById(estacaoId);
        if (estacao == null) return new ArrayList<>();
        Trabalho trabalhoEstacao = estacao.getTrabalho();

        List<Pedido> pedidosAtivos = pedidoDAO.findAllByRestaurante(restauranteId).stream()
                .filter(p -> p.getEstado() != EstadoPedido.ENTREGUE)
                .collect(Collectors.toList());

        List<Tarefa> tarefasParaMostrar = new ArrayList<>();
        for (Pedido p : pedidosAtivos) {
            List<Tarefa> tarefasDoPedido = tarefaDAO.findAllByPedido(p.getId());
            List<Tarefa> tarefasPendentes = tarefasDoPedido.stream()
                    .filter(t -> t.getEstado() == EstadoTarefa.PENDENTE).collect(Collectors.toList());
            List<Tarefa> tarefasIniciadas = tarefasDoPedido.stream()
                    .filter(t -> t.getEstado() != EstadoTarefa.PENDENTE && t.getDataInicio() != null).collect(Collectors.toList());
            
            long maxDuracaoSegundos = tarefasDoPedido.stream()
                    .mapToLong(this::obterDuracaoTarefa).max().orElse(0);

            if (tarefasIniciadas.isEmpty()) {
                for (Tarefa t : tarefasPendentes) {
                    if (verificarTrabalhoTarefa(t, trabalhoEstacao) && obterDuracaoTarefa(t) >= maxDuracaoSegundos) {
                        tarefasParaMostrar.add(t);
                    }
                }
            } else {
                LocalDateTime t0 = tarefasIniciadas.stream().map(Tarefa::getDataInicio).min(LocalDateTime::compareTo).orElse(LocalDateTime.now());
                long segundosDecorridos = ChronoUnit.SECONDS.between(t0, LocalDateTime.now());
                for (Tarefa t : tarefasPendentes) {
                    if (verificarTrabalhoTarefa(t, trabalhoEstacao)) {
                        if (segundosDecorridos >= (maxDuracaoSegundos - obterDuracaoTarefa(t))) {
                            tarefasParaMostrar.add(t);
                        }
                    }
                }
            }
        }
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
            if (t.getDataInicio() == null) t.setDataInicio(LocalDateTime.now());
            tarefaDAO.update(t);

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
            // CORREÇÃO CRÍTICA: Atualizar timestamp final para estatísticas
            p.setDataConclusao(LocalDateTime.now());
            pedidoDAO.update(p);
        }
    }

    private void verificarConclusaoPedido(int pedidoId) {
        List<Tarefa> total = tarefaDAO.findAllByPedido(pedidoId);
        if (!total.isEmpty() && total.stream().allMatch(t -> t.getEstado() == EstadoTarefa.CONCLUIDA)) {
            Pedido p = pedidoDAO.findById(pedidoId);
            if (p != null && p.getEstado() != EstadoPedido.ENTREGUE) {
                // CORREÇÃO: Usar o estado PRONTO definido no Enum
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
            Pedido p = pedidoDAO.findById(t.getPedidoId());
            if (p != null) {
                Ingrediente ing = ingredienteDAO.findById(ingredienteId);
                difundirMensagem(p.getRestauranteId(), "Tarefa #" + t.getId() + " PARADA - Falta " + (ing != null ? ing.getNome() : "Ingrediente"), true);
            }
        }
    }

    @Override
    public List<Ingrediente> listarIngredientesDaTarefa(int tarefaId) {
        Tarefa t = tarefaDAO.findById(tarefaId);
        if (t == null) return List.of();
        Passo p = passoDAO.findById(t.getPassoId());
        return p == null ? List.of() : p.getIngredienteIds().stream().map(ingredienteDAO::findById).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public List<Pedido> consultarPedidosEmProducao(int restauranteId) {
        gerarTarefasParaPedidosNovos(restauranteId);
        return pedidoDAO.findAllByRestaurante(restauranteId).stream()
                .filter(p -> p.getEstado() != EstadoPedido.ENTREGUE)
                .sorted(Comparator.comparingInt(Pedido::getId))
                .collect(Collectors.toList());
    }

    @Override public List<Tarefa> consultarTarefasDoPedido(int pedidoId) { return tarefaDAO.findAllByPedido(pedidoId); }
    @Override public List<Mensagem> consultarMensagens(int restauranteId) { return mensagemDAO.findAllByRestaurante(restauranteId).stream().sorted(Comparator.comparing(Mensagem::getDataHora).reversed()).collect(Collectors.toList()); }
    @Override public void difundirMensagem(int rId, String txt, boolean urg) { Mensagem m = new Mensagem(); m.setRestauranteId(rId); m.setTexto(txt); m.setDataHora(LocalDateTime.now()); mensagemDAO.create(m); }

    // Helpers
    private void gerarTarefasParaPedidosNovos(int restauranteId) {
        List<Pedido> confirmados = pedidoDAO.findAllByRestaurante(restauranteId).stream()
                .filter(p -> p.getEstado() == EstadoPedido.CONFIRMADO).collect(Collectors.toList());
        for (Pedido p : confirmados) {
            boolean criou = false;
            for (LinhaPedido lp : p.getLinhas()) {
                if (lp.getTipo() == TipoItem.PRODUTO) {
                    Produto prod = produtoDAO.findById(lp.getItemId());
                    if (prod != null) criou |= criarTarefasProduto(p.getId(), prod, lp.getQuantidade());
                } else {
                    Menu m = menuDAO.findById(lp.getItemId());
                    if (m != null) for (LinhaMenu lm : m.getLinhas()) {
                        Produto prod = produtoDAO.findById(lm.getProdutoId());
                        if (prod != null) criou |= criarTarefasProduto(p.getId(), prod, lm.getQuantidade() * lp.getQuantidade());
                    }
                }
            }
            if (criou) { p.setEstado(EstadoPedido.EM_PREPARACAO); pedidoDAO.update(p); }
        }
    }

    private boolean criarTarefasProduto(int pedidoId, Produto prod, int quantidade) {
        boolean criou = false;
        for (int i = 0; i < quantidade; i++) {
            for (Integer passoId : prod.getPassoIds()) {
                Tarefa t = new Tarefa();
                t.setPedidoId(pedidoId); t.setProdutoId(prod.getId()); t.setPassoId(passoId);
                t.setDataCriacao(LocalDateTime.now()); t.setEstado(EstadoTarefa.PENDENTE);
                tarefaDAO.create(t);
                criou = true;
            }
        }
        return criou;
    }

    private long obterDuracaoTarefa(Tarefa t) { Passo p = passoDAO.findById(t.getPassoId()); return (p != null && p.getDuracao() != null) ? p.getDuracao().toSeconds() : 0; }
    private boolean verificarTrabalhoTarefa(Tarefa t, Trabalho trab) { Passo p = passoDAO.findById(t.getPassoId()); return p != null && p.getTrabalho() == trab; }

    @Override
    public void reportarPedidoIncorreto(int pedidoId) {
        Pedido p = pedidoDAO.findById(pedidoId);
        if (p != null) {
            difundirMensagem(p.getRestauranteId(), "Pedido #" + pedidoId + " reportado como INCORRETO pelo cliente.", true);
        }
    }

    @Override
    public void gerarTarefasCorrecao(int pedidoId) {
        Pedido p = pedidoDAO.findById(pedidoId);
        if (p != null) {
            boolean criou = false;
            for (LinhaPedido lp : p.getLinhas()) {
                if (lp.getTipo() == TipoItem.PRODUTO) {
                    Produto prod = produtoDAO.findById(lp.getItemId());
                    if (prod != null) criou |= criarTarefasProduto(p.getId(), prod, lp.getQuantidade());
                } else {
                    Menu m = menuDAO.findById(lp.getItemId());
                    if (m != null) for (LinhaMenu lm : m.getLinhas()) {
                        Produto prod = produtoDAO.findById(lm.getProdutoId());
                        if (prod != null) criou |= criarTarefasProduto(p.getId(), prod, lm.getQuantidade() * lp.getQuantidade());
                    }
                }
            }
            if (criou) {
                difundirMensagem(p.getRestauranteId(), "Tarefas de correção geradas para o Pedido #" + pedidoId + ".", true);
            }
        }
    }

    @Override
    public void verificarPedidosEsquecidos(int restauranteId) {
        List<Pedido> pedidos = pedidoDAO.findAllByRestaurante(restauranteId);
        LocalDateTime agora = LocalDateTime.now();
        
        for (Pedido p : pedidos) {
            // Check orders that are PRONTO (ready) but not yet delivered
            if (p.getEstado() == EstadoPedido.PRONTO && p.getDataCriacao() != null) {
                long minutosEspera = ChronoUnit.MINUTES.between(p.getDataCriacao(), agora);
                
                // Alert if order has been ready for more than 10 minutes
                if (minutosEspera > 10) {
                    difundirMensagem(restauranteId, 
                        "ALERTA: Pedido #" + p.getId() + " está pronto há " + minutosEspera + " minutos!", 
                        true);
                }
            }
            
            // Check orders in preparation that are taking too long
            if (p.getEstado() == EstadoPedido.EM_PREPARACAO && p.getDataCriacao() != null) {
                long minutosEmPreparacao = ChronoUnit.MINUTES.between(p.getDataCriacao(), agora);
                
                // Alert if order has been in preparation for more than 30 minutes
                if (minutosEmPreparacao > 30) {
                    difundirMensagem(restauranteId, 
                        "ALERTA: Pedido #" + p.getId() + " em preparação há " + minutosEmPreparacao + " minutos!", 
                        true);
                }
            }
        }
    }
}
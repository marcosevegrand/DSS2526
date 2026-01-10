package dss2526.service.producao;

import dss2526.data.contract.*;
import dss2526.data.impl.*;
import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Facade para o módulo de Produção.
 * Singleton que oferece operações de gestão de tarefas, cozinha e caixa.
 *
 * Esta classe implementa APENAS os métodos necessários conforme o Interface Segregation Principle.
 */
public class ProducaoFacade implements IProducaoFacade {
    private static ProducaoFacade instance;

    // ============ DAOs NECESSÁRIOS ============
    protected final RestauranteDAO restauranteDAO = RestauranteDAOImpl.getInstance();
    protected final EstacaoDAO estacaoDAO = EstacaoDAOImpl.getInstance();
    protected final PedidoDAO pedidoDAO = PedidoDAOImpl.getInstance();
    protected final TarefaDAO tarefaDAO = TarefaDAOImpl.getInstance();
    protected final PassoDAO passoDAO = PassoDAOImpl.getInstance();
    protected final IngredienteDAO ingredienteDAO = IngredienteDAOImpl.getInstance();
    protected final MensagemDAO mensagemDAO = MensagemDAOImpl.getInstance();
    protected final ProdutoDAO produtoDAO = ProdutoDAOImpl.getInstance();
    protected final MenuDAO menuDAO = MenuDAOImpl.getInstance();
    protected final PagamentoDAO pagamentoDAO = PagamentoDAOImpl.getInstance();

    /**
     * Construtor privado (padrão Singleton).
     */
    private ProducaoFacade() {}

    /**
     * Obtém a instância única (thread-safe).
     */
    public static synchronized ProducaoFacade getInstance() {
        if (instance == null) {
            instance = new ProducaoFacade();
        }
        return instance;
    }

    // ============ IMPLEMENTAÇÃO DOS MÉTODOS ============

    @Override
    public List<Restaurante> listarRestaurantes() {
        return restauranteDAO.findAll();
    }

    @Override
    public List<Estacao> listarEstacoesDeRestaurante(int restauranteId) {
        return estacaoDAO.findAllByRestaurante(restauranteId);
    }

    @Override
    public Estacao obterEstacao(int estacaoId) {
        return estacaoDAO.findById(estacaoId);
    }

    @Override
    public Passo obterPasso(int passoId) {
        return passoDAO.findById(passoId);
    }

    @Override
    public Pedido obterPedido(int pedidoId) {
        return pedidoDAO.findById(pedidoId);
    }

    @Override
    public Produto obterProduto(int produtoId) {
        return produtoDAO.findById(produtoId);
    }

    @Override
    public Menu obterMenu(int menuId) {
        return menuDAO.findById(menuId);
    }

    @Override
    public List<Tarefa> listarTarefasParaIniciar(int rId, int eId) {
        // Primeiro, gerar tarefas para pedidos confirmados
        verificarNovosPedidos(rId);

        Estacao est = estacaoDAO.findById(eId);
        if (est == null) {
            return Collections.emptyList();
        }

        return tarefaDAO.findAll().stream()
                .filter(t -> t.getEstado() == EstadoTarefa.PENDENTE)
                .filter(t -> {
                    Pedido p = pedidoDAO.findById(t.getPedidoId());
                    return p != null && p.getRestauranteId() == rId;
                })
                .filter(t -> {
                    // CORRIGIDO: Null check no passo
                    Passo passo = passoDAO.findById(t.getPassoId());
                    return passo != null && est.podeConfecionar(passo.getTrabalho());
                })
                .filter(this::isTarefaSincronizada)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tarefa> listarTarefasEmExecucao(int estacaoId) {
        return tarefaDAO.findAll().stream()
                .filter(t -> t.getEstado() == EstadoTarefa.EM_EXECUCAO || t.getEstado() == EstadoTarefa.ATRASADA)
                .filter(t -> t.getEstacaoId() == estacaoId)
                .collect(Collectors.toList());
    }

    /**
     * Auxiliar privado: Verifica sincronização de tarefa.
     * Garante que tarefas mais curtas só começam quando as mais longas estão próximas do fim.
     */
    private boolean isTarefaSincronizada(Tarefa t) {
        List<Tarefa> todas = tarefaDAO.findAllByPedido(t.getPedidoId());

        // Calcular duração máxima de todas as tarefas do pedido
        long maxDuracaoTotal = todas.stream()
                .mapToLong(x -> {
                    Passo p = passoDAO.findById(x.getPassoId());
                    return p != null ? p.getDuracao().toMinutes() : 0;
                })
                .max().orElse(0);

        Passo meuPasso = passoDAO.findById(t.getPassoId());
        long minhaDuracao = meuPasso != null ? meuPasso.getDuracao().toMinutes() : 0;

        // Verificar se já existe uma tarefa longa em execução
        Optional<Tarefa> longaEmExecucao = todas.stream()
                .filter(x -> x.getEstado() == EstadoTarefa.EM_EXECUCAO || x.getEstado() == EstadoTarefa.ATRASADA)
                .filter(x -> {
                    Passo p = passoDAO.findById(x.getPassoId());
                    return p != null && p.getDuracao().toMinutes() == maxDuracaoTotal;
                })
                .findFirst();

        if (longaEmExecucao.isEmpty()) {
            // Se não há tarefa longa em execução:
            // - Permitir início da tarefa mais longa
            // - Ou permitir se nenhuma tarefa está em execução
            return minhaDuracao == maxDuracaoTotal ||
                    todas.stream().noneMatch(x -> x.getEstado() == EstadoTarefa.EM_EXECUCAO);
        } else {
            // Se há tarefa longa em execução, calcular tempo restante
            LocalDateTime inicio = longaEmExecucao.get().getDataInicio();
            if (inicio == null) {
                return false;
            }
            long decorrido = Duration.between(inicio, LocalDateTime.now()).toMinutes();
            long faltaParaLonga = Math.max(0, maxDuracaoTotal - decorrido);
            return minhaDuracao >= faltaParaLonga;
        }
    }

    /**
     * Auxiliar privado: Verifica novos pedidos confirmados e gera tarefas.
     */
    private void verificarNovosPedidos(int rId) {
        pedidoDAO.findAllByRestaurante(rId).stream()
                .filter(p -> p.getEstado() == EstadoPedido.CONFIRMADO)
                .forEach(p -> {
                    p.getLinhas().forEach(l -> gerarTarefas(p.getId(), l));
                    p.setEstado(EstadoPedido.EM_PREPARACAO);
                    pedidoDAO.update(p);
                });
    }

    /**
     * Auxiliar privado: Gera tarefas para um item do pedido.
     */
    private void gerarTarefas(int pId, LinhaPedido lp) {
        if (lp.getTipo() == TipoItem.PRODUTO) {
            Produto pr = produtoDAO.findById(lp.getItemId());
            if (pr != null && !pr.getPassoIds().isEmpty()) {
                // Criar uma tarefa para cada passo, para cada quantidade
                for (int i = 0; i < lp.getQuantidade(); i++) {
                    for (Integer passoId : pr.getPassoIds()) {
                        Tarefa t = new Tarefa();
                        t.setPedidoId(pId);
                        t.setProdutoId(pr.getId());
                        t.setPassoId(passoId);
                        t.setEstado(EstadoTarefa.PENDENTE);
                        t.setDataCriacao(LocalDateTime.now());
                        tarefaDAO.create(t);
                    }
                }
            }
        } else {
            // Menu: expandir para os produtos que compõem o menu
            Menu m = menuDAO.findById(lp.getItemId());
            if (m != null) {
                m.getLinhas().forEach(lm -> {
                    LinhaPedido dummy = new LinhaPedido();
                    dummy.setItemId(lm.getProdutoId());
                    dummy.setTipo(TipoItem.PRODUTO);
                    dummy.setQuantidade(lm.getQuantidade() * lp.getQuantidade());
                    gerarTarefas(pId, dummy);
                });
            }
        }
    }

    @Override
    public void iniciarTarefa(int tId, int estacaoId) {
        Tarefa t = tarefaDAO.findById(tId);
        if (t != null && (t.getEstado() == EstadoTarefa.PENDENTE || t.getEstado() == EstadoTarefa.ATRASADA)) {
            t.setEstado(EstadoTarefa.EM_EXECUCAO);
            t.setEstacaoId(estacaoId);
            t.setDataInicio(LocalDateTime.now());
            tarefaDAO.update(t);
        }
    }

    @Override
    public void concluirTarefa(int tId) {
        Tarefa t = tarefaDAO.findById(tId);
        if (t == null) {
            return;
        }

        t.setEstado(EstadoTarefa.CONCLUIDA);
        t.setDataConclusao(LocalDateTime.now());
        tarefaDAO.update(t);

        // Verificar se todas as tarefas do pedido estão concluídas
        List<Tarefa> todas = tarefaDAO.findAllByPedido(t.getPedidoId());
        if (todas.stream().allMatch(x -> x.getEstado() == EstadoTarefa.CONCLUIDA)) {
            Pedido p = pedidoDAO.findById(t.getPedidoId());
            if (p != null) {
                p.setEstado(EstadoPedido.PRONTO);
                pedidoDAO.update(p);
            }
        }
    }

    @Override
    public List<Ingrediente> listarIngredientesDaTarefa(int tarefaId) {
        Tarefa t = tarefaDAO.findById(tarefaId);
        if (t == null) {
            return Collections.emptyList();
        }

        Set<Integer> ingredienteIds = new HashSet<>();

        // Ingredientes do passo
        Passo passo = passoDAO.findById(t.getPassoId());
        if (passo != null && !passo.getIngredienteIds().isEmpty()) {
            ingredienteIds.addAll(passo.getIngredienteIds());
        }

        // Ingredientes do produto (se o passo não tiver ingredientes específicos)
        if (ingredienteIds.isEmpty()) {
            Produto p = produtoDAO.findById(t.getProdutoId());
            if (p != null) {
                p.getLinhas().forEach(lp -> ingredienteIds.add(lp.getIngredienteId()));
            }
        }

        return ingredienteIds.stream()
                .map(ingredienteDAO::findById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void atrasarTarefa(int tId, int iId) {
        Tarefa t = tarefaDAO.findById(tId);
        if (t == null) {
            return;
        }

        t.setEstado(EstadoTarefa.ATRASADA);
        tarefaDAO.update(t);

        // Criar mensagem de alerta
        Ingrediente ing = ingredienteDAO.findById(iId);
        Pedido ped = pedidoDAO.findById(t.getPedidoId());

        if (ped != null) {
            Mensagem m = new Mensagem();
            m.setRestauranteId(ped.getRestauranteId());
            m.setTexto("ALERTA COZINHA: Falta de " +
                    (ing != null ? ing.getNome() : "Ingrediente ID " + iId) +
                    " para o Pedido #" + ped.getId() + " (Tarefa #" + tId + ")");
            m.setDataHora(LocalDateTime.now());
            mensagemDAO.create(m);
        }
    }

    @Override
    public List<Pedido> listarAguardaPagamento(int rId) {
        return pedidoDAO.findAllByRestaurante(rId).stream()
                .filter(p -> p.getEstado() == EstadoPedido.AGUARDA_PAGAMENTO)
                .collect(Collectors.toList());
    }

    @Override
    public Duration processarPagamentoCaixa(int pId) {
        Pagamento pag = pagamentoDAO.findByPedido(pId);
        Pedido p = pedidoDAO.findById(pId);

        if (pag == null || p == null) {
            return Duration.ZERO;
        }

        // 1. Confirmar Pagamento
        pag.setConfirmado(true);
        pag.setData(LocalDateTime.now());
        pagamentoDAO.update(pag);

        // 2. Confirmar Pedido
        p.setEstado(EstadoPedido.CONFIRMADO);
        pedidoDAO.update(p);

        // 3. Gerar Tarefas
        verificarNovosPedidos(p.getRestauranteId());

        // 4. Calcular Estimativa
        List<Tarefa> tarefasDoPedido = tarefaDAO.findAllByPedido(pId);

        long maxMinutos = tarefasDoPedido.stream()
                .mapToLong(t -> {
                    Passo passo = passoDAO.findById(t.getPassoId());
                    return passo != null ? passo.getDuracao().toMinutes() : 0;
                })
                .max()
                .orElse(0);

        return Duration.ofMinutes(maxMinutos + 5);
    }

    @Override
    public List<Pedido> listarProntos(int rId) {
        return pedidoDAO.findAllByRestaurante(rId).stream()
                .filter(p -> p.getEstado() == EstadoPedido.PRONTO)
                .collect(Collectors.toList());
    }

    @Override
    public void confirmarEntrega(int pId) {
        Pedido p = pedidoDAO.findById(pId);
        if (p != null && p.getEstado() == EstadoPedido.PRONTO) {
            p.setEstado(EstadoPedido.ENTREGUE);
            p.setDataConclusao(LocalDateTime.now());
            pedidoDAO.update(p);
        }
    }

    @Override
    public void solicitarRefacaoItens(int pId, List<Integer> lIds) {
        Pedido p = pedidoDAO.findById(pId);
        if (p == null || lIds == null || lIds.isEmpty()) {
            return;
        }

        // Gerar novas tarefas para os itens a refazer
        p.getLinhas().stream()
                .filter(l -> lIds.contains(l.getId()))
                .forEach(l -> gerarTarefas(pId, l));

        // Reverter estado do pedido
        p.setEstado(EstadoPedido.EM_PREPARACAO);
        p.setDataConclusao(null);
        pedidoDAO.update(p);

        // Criar mensagem de alerta
        Mensagem m = new Mensagem();
        m.setRestauranteId(p.getRestauranteId());
        m.setTexto("DEVOLUÇÃO: Itens do Pedido #" + pId + " devolvidos para refazer.");
        m.setDataHora(LocalDateTime.now());
        mensagemDAO.create(m);
    }

    @Override
    public Map<Pedido, String> obterProgressoMonitor(int rId) {
        return pedidoDAO.findAllByRestaurante(rId).stream()
                .filter(p -> p.getEstado() == EstadoPedido.CONFIRMADO ||
                        p.getEstado() == EstadoPedido.EM_PREPARACAO ||
                        p.getEstado() == EstadoPedido.PRONTO)
                .collect(Collectors.toMap(
                        p -> p,
                        p -> {
                            List<Tarefa> tfs = tarefaDAO.findAllByPedido(p.getId());
                            if (tfs.isEmpty()) {
                                return "Aguardando Início";
                            }
                            long concluidas = tfs.stream()
                                    .filter(t -> t.getEstado() == EstadoTarefa.CONCLUIDA)
                                    .count();
                            return concluidas + "/" + tfs.size() + " tarefas";
                        }
                ));
    }

    @Override
    public List<Mensagem> listarMensagens(int rId) {
        return mensagemDAO.findAllByRestaurante(rId).stream()
                .sorted(Comparator.comparing(Mensagem::getDataHora).reversed())
                .collect(Collectors.toList());
    }
}
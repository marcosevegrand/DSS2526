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
 * Esta classe implementa APENAS os métodos necessários, não herdando de BaseFacade.
 */
public class ProducaoFacade implements IProducaoFacade {
    private static ProducaoFacade instance;
    
    // ============ DAOs NECESSÁRIOS (9 de 12) ============
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
        return estacaoDAO.findAll().stream()
                .filter(e -> e.getRestauranteId() == restauranteId)
                .collect(Collectors.toList());
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
        verificarNovosPedidos(rId);
        Estacao est = estacaoDAO.findById(eId);
        if (est == null) return Collections.emptyList();
        
        return tarefaDAO.findAll().stream()
            .filter(t -> t.getEstado() == EstadoTarefa.PENDENTE)
            .filter(t -> {
                Pedido p = pedidoDAO.findById(t.getPedidoId());
                return p != null && p.getRestauranteId() == rId;
            })
            .filter(t -> est.podeConfecionar(passoDAO.findById(t.getPassoId()).getTrabalho()))
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
     */
    private boolean isTarefaSincronizada(Tarefa t) {
        List<Tarefa> todas = tarefaDAO.findAllByPedido(t.getPedidoId());
        
        long maxDuracaoTotal = todas.stream()
                .mapToLong(x -> passoDAO.findById(x.getPassoId()).getDuracao().toMinutes())
                .max().orElse(0);
        
        long minhaDuracao = passoDAO.findById(t.getPassoId()).getDuracao().toMinutes();

        Optional<Tarefa> longaEmExecucao = todas.stream()
                .filter(x -> x.getEstado() == EstadoTarefa.EM_EXECUCAO || x.getEstado() == EstadoTarefa.ATRASADA)
                .filter(x -> passoDAO.findById(x.getPassoId()).getDuracao().toMinutes() == maxDuracaoTotal)
                .findFirst();

        if (longaEmExecucao.isEmpty()) {
            return minhaDuracao == maxDuracaoTotal || todas.stream().noneMatch(x -> x.getEstado() == EstadoTarefa.EM_EXECUCAO);
        } else {
            long decorrido = Duration.between(longaEmExecucao.get().getDataInicio(), LocalDateTime.now()).toMinutes();
            long faltaParaLonga = Math.max(0, maxDuracaoTotal - decorrido);
            return minhaDuracao >= faltaParaLonga;
        }
    }

    /**
     * Auxiliar privado: Verifica novos pedidos e gera tarefas.
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
            if (pr != null) {
                for (int i = 0; i < lp.getQuantidade(); i++) {
                    pr.getPassoIds().forEach(sid -> {
                        Tarefa t = new Tarefa();
                        t.setPedidoId(pId);
                        t.setProdutoId(pr.getId());
                        t.setPassoId(sid);
                        t.setEstado(EstadoTarefa.PENDENTE);
                        t.setDataCriacao(LocalDateTime.now());
                        tarefaDAO.create(t);
                    });
                }
            }
        } else {
            Menu m = menuDAO.findById(lp.getItemId());
            if (m != null) {
                m.getLinhas().forEach(lm -> {
                    LinhaPedido d = new LinhaPedido();
                    d.setItemId(lm.getProdutoId());
                    d.setTipo(TipoItem.PRODUTO);
                    d.setQuantidade(lm.getQuantidade() * lp.getQuantidade());
                    gerarTarefas(pId, d);
                });
            }
        }
    }

    @Override
    public void iniciarTarefa(int tId, int estacaoId) {
        Tarefa t = tarefaDAO.findById(tId);
        if (t != null) {
            t.setEstado(EstadoTarefa.EM_EXECUCAO);
            t.setEstacaoId(estacaoId);
            t.setDataInicio(LocalDateTime.now());
            tarefaDAO.update(t);
        }
    }

    @Override
    public void concluirTarefa(int tId) {
        Tarefa t = tarefaDAO.findById(tId);
        if (t == null) return;

        t.setEstado(EstadoTarefa.CONCLUIDA);
        t.setDataConclusao(LocalDateTime.now());
        tarefaDAO.update(t);
        
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
        if (t == null) return new ArrayList<>();
        
        Passo passo = passoDAO.findById(t.getPassoId());
        if (passo != null && !passo.getIngredienteIds().isEmpty()) {
            return passo.getIngredienteIds().stream()
                    .map(ingredienteDAO::findById).filter(Objects::nonNull).collect(Collectors.toList());
        }
        Produto p = produtoDAO.findById(t.getProdutoId());
        if (p != null) {
            return p.getLinhas().stream()
                    .map(lp -> ingredienteDAO.findById(lp.getIngredienteId())).filter(Objects::nonNull)
                    .distinct().collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public void atrasarTarefa(int tId, int iId) {
        Tarefa t = tarefaDAO.findById(tId);
        if (t == null) return;

        t.setEstado(EstadoTarefa.ATRASADA);
        tarefaDAO.update(t);
        
        Ingrediente ing = ingredienteDAO.findById(iId);
        Pedido ped = pedidoDAO.findById(t.getPedidoId());
        
        Mensagem m = new Mensagem();
        m.setRestauranteId(ped.getRestauranteId());
        m.setTexto("ALERTA COZINHA: Falta de " + (ing != null ? ing.getNome() : "Ingrediente ID " + iId) + 
                   " para o Pedido #" + ped.getId() + " (Tarefa #" + tId + ")");
        m.setDataHora(LocalDateTime.now());
        mensagemDAO.create(m);
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
        
        if (pag != null && p != null) {
            // 1. Confirma Pagamento
            pag.setConfirmado(true);
            pag.setData(LocalDateTime.now());
            pagamentoDAO.update(pag);
            
            // 2. Confirma Pedido
            p.setEstado(EstadoPedido.CONFIRMADO);
            pedidoDAO.update(p);
            
            // 3. Gera Tarefas
            verificarNovosPedidos(p.getRestauranteId());
            
            // 4. Calcula Estimativa
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
        return Duration.ZERO;
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
        if (p != null) {
            p.setEstado(EstadoPedido.ENTREGUE);
            p.setDataConclusao(LocalDateTime.now());
            pedidoDAO.update(p);
        }
    }

    @Override
    public void solicitarRefacaoItens(int pId, List<Integer> lIds) {
        Pedido p = pedidoDAO.findById(pId);
        if (p != null) {
            p.getLinhas().stream().filter(l -> lIds.contains(l.getId())).forEach(l -> gerarTarefas(pId, l));
            p.setEstado(EstadoPedido.EM_PREPARACAO);
            p.setDataConclusao(null);
            pedidoDAO.update(p);
            
            Mensagem m = new Mensagem();
            m.setRestauranteId(p.getRestauranteId());
            m.setTexto("DEVOLUÇÃO: Itens do Pedido #" + pId + " devolvidos para refazer.");
            m.setDataHora(LocalDateTime.now());
            mensagemDAO.create(m);
        }
    }
    
    @Override
    public Map<Pedido, String> obterProgressoMonitor(int rId) {
        return pedidoDAO.findAllByRestaurante(rId).stream()
            .filter(p -> p.getEstado() == EstadoPedido.CONFIRMADO || 
                        p.getEstado() == EstadoPedido.EM_PREPARACAO || 
                        p.getEstado() == EstadoPedido.PRONTO)
            .collect(Collectors.toMap(p -> p, p -> {
                List<Tarefa> tfs = tarefaDAO.findAllByPedido(p.getId());
                long concluidas = tfs.stream().filter(t -> t.getEstado() == EstadoTarefa.CONCLUIDA).count();
                if (tfs.isEmpty()) return "Aguardando Início";
                return concluidas + "/" + tfs.size() + " tarefas";
            }));
    }
    
    @Override
    public List<Mensagem> listarMensagens(int rId) {
        return mensagemDAO.findAllByRestaurante(rId).stream()
                .sorted(Comparator.comparing(Mensagem::getDataHora).reversed())
                .collect(Collectors.toList());
    }
}

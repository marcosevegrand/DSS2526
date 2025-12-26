package dss2526.service.producao;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dss2526.domain.entity.Estacao;
import dss2526.domain.entity.LinhaMenu;
import dss2526.domain.entity.LinhaPedido;
import dss2526.domain.entity.LinhaProduto;
import dss2526.domain.entity.LinhaStock;
import dss2526.domain.entity.Menu;
import dss2526.domain.entity.Passo;
import dss2526.domain.entity.Pedido;
import dss2526.domain.entity.Produto;
import dss2526.domain.entity.Restaurante;
import dss2526.domain.entity.Tarefa;
import dss2526.domain.enumeration.EstadoPedido;
import dss2526.domain.enumeration.TipoItem;
import dss2526.domain.enumeration.Trabalho;
import dss2526.service.base.BaseFacade;

public class ProducaoFacade extends BaseFacade implements IProducaoFacade {

    private static ProducaoFacade instance;

    private ProducaoFacade() {
    }

    public static synchronized ProducaoFacade getInstance() {
        if (instance == null) {
            instance = new ProducaoFacade();
        }
        return instance;
    }

    // // --- Order Processing & Task Generation ---

    // @Override
    // public void iniciarProducaoPedido(Integer pedidoId) {
    //     Pedido pedido = pedidoDAO.findById(pedidoId);
    //     if (pedido == null) {
    //         throw new IllegalArgumentException("Pedido não encontrado: " + pedidoId);
    //     }
        
    //     if (pedido.getEstado() != EstadoPedido.CONFIRMADO) {
    //         throw new IllegalStateException("Pedido deve estar no estado CONFIRMADO para iniciar produção");
    //     }
        
    //     // Gerar tarefas para todos os produtos do pedido
    //     List<Tarefa> tarefas = gerarTarefasParaPedido(pedidoId);
        
    //     // Atualizar estado do pedido
    //     pedido.setEstado(EstadoPedido.EM_PREPARACAO);
    //     pedidoDAO.update(pedido);
    // }

    // @Override
    // public List<Tarefa> gerarTarefasParaPedido(Integer pedidoId) {
    //     Pedido pedido = pedidoDAO.findById(pedidoId);
    //     if (pedido == null) {
    //         throw new IllegalArgumentException("Pedido não encontrado: " + pedidoId);
    //     }
        
    //     List<Tarefa> tarefasGeradas = new ArrayList<>();
        
    //     // Para cada item no pedido
    //     for (LinhaPedido linhaPedido : pedido.getLinhas()) {
    //         // Determinar se é produto ou menu
    //         if (linhaPedido.getTipo() == TipoItem.PRODUTO) {
    //             tarefasGeradas.addAll(gerarTarefasParaProduto(pedidoId, linhaPedido.getItemId()));
    //         } else if (linhaPedido.getTipo() == TipoItem.MENU) {
    //             // Buscar produtos do menu
    //             Menu menu = menuDAO.findById(linhaPedido.getItemId());
    //             if (menu != null) {
    //                 for (LinhaMenu linhaMenu : menu.getLinha()) {
    //                     tarefasGeradas.addAll(gerarTarefasParaProduto(pedidoId, linhaMenu.getProdutoId()));
    //                 }
    //             }
    //         }
    //     }
        
    //     return tarefasGeradas;
    // }
    
    // private List<Tarefa> gerarTarefasParaProduto(Integer pedidoId, Integer produtoId) {
    //     List<Tarefa> tarefas = new ArrayList<>();
    //     Produto produto = produtoDAO.findById(produtoId);
    //     if (produto == null) return tarefas;
        
    //     // Para cada passo do produto
    //     for (Integer passoId : produto.getPassoIds()) {
    //         Passo passo = passoDAO.findById(passoId);
    //         if (passo == null) continue;
            
    //         // Criar tarefa
    //         Tarefa tarefa = new Tarefa();
    //         tarefa.setPedidoId(pedidoId);
    //         tarefa.setProdutoId(produto.getId());
    //         tarefa.setPassoId(passoId);
    //         tarefa.setDataCriacao(LocalDateTime.now());
    //         tarefa.setConcluido(false);
            
    //         tarefaDAO.create(tarefa);
    //         tarefas.add(tarefa);
    //     }
        
    //     return tarefas;
    // }

    // @Override
    // public void cancelarProducaoPedido(Integer pedidoId) {
    //     Pedido pedido = pedidoDAO.findById(pedidoId);
    //     if (pedido == null) {
    //         throw new IllegalArgumentException("Pedido não encontrado: " + pedidoId);
    //     }
        
    //     // Cancelar todas as tarefas pendentes
    //     List<Tarefa> tarefas = tarefaDAO.findAllByPedido(pedidoId);
    //     for (Tarefa tarefa : tarefas) {
    //         if (!tarefa.isConcluido()) {
    //             tarefaDAO.delete(tarefa.getId());
    //         }
    //     }
        
    //     // Atualizar estado do pedido
    //     pedido.setEstado(EstadoPedido.CANCELADO);
    //     pedidoDAO.update(pedido);
    // }

    // // --- Task Assignment & Execution ---

    // @Override
    // public List<Tarefa> obterTarefasPendentesPorEstacao(Integer estacaoId) {
    //     Estacao estacao = estacaoDAO.findById(estacaoId);
    //     if (estacao == null) {
    //         throw new IllegalArgumentException("Estação não encontrada: " + estacaoId);
    //     }
        
    //     Trabalho trabalhoEstacao = estacao.getTrabalho();
    //     return obterTarefasPendentesPorTrabalho(trabalhoEstacao);
    // }

    // @Override
    // public List<Tarefa> obterTarefasPendentesPorTrabalho(Trabalho trabalho) {
    //     List<Tarefa> todasTarefas = tarefaDAO.findAll();
        
    //     return todasTarefas.stream()
    //             .filter(tarefa -> !tarefa.isConcluido())
    //             .filter(tarefa -> {
    //                 Passo passo = passoDAO.findById(tarefa.getPassoId());
    //                 return passo != null && passo.getTrabalho() == trabalho;
    //             })
    //             .collect(Collectors.toList());
    // }

    // @Override
    // public void iniciarTarefa(Integer tarefaId) {
    //     Tarefa tarefa = tarefaDAO.findById(tarefaId);
    //     if (tarefa == null) {
    //         throw new IllegalArgumentException("Tarefa não encontrada: " + tarefaId);
    //     }
        
    //     if (tarefa.isConcluido()) {
    //         throw new IllegalStateException("Tarefa já está concluída");
    //     }
        
    //     // Marcar início (pode adicionar timestamp de início se houver campo)
    //     // Por enquanto, apenas garantir que não está concluída
    //     tarefaDAO.update(tarefa);
    // }

    // @Override
    // public void concluirTarefa(Integer tarefaId) {
    //     Tarefa tarefa = tarefaDAO.findById(tarefaId);
    //     if (tarefa == null) {
    //         throw new IllegalArgumentException("Tarefa não encontrada: " + tarefaId);
    //     }
        
    //     if (tarefa.isConcluido()) {
    //         throw new IllegalStateException("Tarefa já está concluída");
    //     }
        
    //     // Marcar como concluída
    //     tarefa.setConcluido(true);
    //     tarefa.setDataConclusao(LocalDateTime.now());
    //     tarefaDAO.update(tarefa);
        
    //     // Verificar se todas as tarefas do pedido foram concluídas
    //     Integer pedidoId = tarefa.getPedidoId();
    //     if (todosProdutosConcluidos(pedidoId)) {
    //         atualizarEstadoPedido(pedidoId, EstadoPedido.PRONTO);
    //     }
    // }

    // @Override
    // public void marcarTarefaAtrasada(Integer tarefaId) {
    //     Tarefa tarefa = tarefaDAO.findById(tarefaId);
    //     if (tarefa == null) {
    //         throw new IllegalArgumentException("Tarefa não encontrada: " + tarefaId);
    //     }
        
    //     // Lógica para marcar como atrasada
    //     // (Pode ser baseada em comparação de tempo esperado vs tempo decorrido)
    //     tarefaDAO.update(tarefa);
    // }

    // // --- Order Status Management ---

    // @Override
    // public void atualizarEstadoPedido(Integer pedidoId, EstadoPedido novoEstado) {
    //     Pedido pedido = pedidoDAO.findById(pedidoId);
    //     if (pedido == null) {
    //         throw new IllegalArgumentException("Pedido não encontrado: " + pedidoId);
    //     }
        
    //     pedido.setEstado(novoEstado);
    //     pedidoDAO.update(pedido);
    // }

    // @Override
    // public EstadoPedido verificarEstadoPedido(Integer pedidoId) {
    //     Pedido pedido = pedidoDAO.findById(pedidoId);
    //     if (pedido == null) {
    //         throw new IllegalArgumentException("Pedido não encontrado: " + pedidoId);
    //     }
    //     return pedido.getEstado();
    // }

    // @Override
    // public boolean todosProdutosConcluidos(Integer pedidoId) {
    //     List<Tarefa> tarefas = tarefaDAO.findAllByPedido(pedidoId);
        
    //     if (tarefas.isEmpty()) {
    //         return false;
    //     }
        
    //     return tarefas.stream().allMatch(Tarefa::isConcluido);
    // }

    // // --- Task Queue Management ---

    // @Override
    // public List<Tarefa> listarTarefasPorEstado(EstadoTarefa estado) {
    //     // Nota: A entidade Tarefa usa boolean 'concluido', não EstadoTarefa enum
    //     // Adaptando a lógica baseado no que existe
    //     List<Tarefa> todas = tarefaDAO.findAll();
        
    //     switch (estado) {
    //         case CONCLUIDA:
    //             return todas.stream()
    //                     .filter(Tarefa::isConcluido)
    //                     .collect(Collectors.toList());
    //         case PENDENTE:
    //         case EM_ANDAMENTO:
    //             return todas.stream()
    //                     .filter(t -> !t.isConcluido())
    //                     .collect(Collectors.toList());
    //         case ATRASADA:
    //             return listarTarefasAtrasadas();
    //         default:
    //             return new ArrayList<>();
    //     }
    // }

    // @Override
    // public List<Tarefa> listarTarefasEmAndamento() {
    //     // Como não há campo específico, consideramos não concluídas como em andamento
    //     return tarefaDAO.findAll().stream()
    //             .filter(t -> !t.isConcluido())
    //             .collect(Collectors.toList());
    // }

    // @Override
    // public List<Tarefa> listarTarefasPendentes() {
    //     return tarefaDAO.findAll().stream()
    //             .filter(t -> !t.isConcluido())
    //             .collect(Collectors.toList());
    // }

    // @Override
    // public List<Tarefa> listarTarefasAtrasadas() {
    //     List<Tarefa> atrasadas = new ArrayList<>();
    //     List<Tarefa> pendentes = listarTarefasPendentes();
        
    //     LocalDateTime agora = LocalDateTime.now();
        
    //     for (Tarefa tarefa : pendentes) {
    //         Passo passo = passoDAO.findById(tarefa.getPassoId());
    //         if (passo != null && passo.getDuracao() != null) {
    //             LocalDateTime tempoEsperado = tarefa.getDataCriacao().plus(passo.getDuracao());
    //             if (agora.isAfter(tempoEsperado)) {
    //                 atrasadas.add(tarefa);
    //             }
    //         }
    //     }
        
    //     return atrasadas;
    // }

    // @Override
    // public int contarTarefasPendentesPorEstacao(Integer estacaoId) {
    //     return obterTarefasPendentesPorEstacao(estacaoId).size();
    // }

    // // --- Production Monitoring ---

    // @Override
    // public List<Pedido> listarPedidosEmProducao(Integer restauranteId) {
    //     List<Pedido> todosPedidos = pedidoDAO.findAll();
        
    //     return todosPedidos.stream()
    //             .filter(p -> p.getRestauranteId() == restauranteId)
    //             .filter(p -> p.getEstado() == EstadoPedido.EM_PREPARACAO)
    //             .collect(Collectors.toList());
    // }

    // @Override
    // public List<Pedido> listarPedidosProntos(Integer restauranteId) {
    //     List<Pedido> todosPedidos = pedidoDAO.findAll();
        
    //     return todosPedidos.stream()
    //             .filter(p -> p.getRestauranteId() == restauranteId)
    //             .filter(p -> p.getEstado() == EstadoPedido.PRONTO)
    //             .collect(Collectors.toList());
    // }

    // @Override
    // public double calcularTempoMedioConclusao(Integer restauranteId) {
    //     List<Pedido> pedidosConcluidos = pedidoDAO.findAll().stream()
    //             .filter(p -> p.getRestauranteId() == restauranteId)
    //             .filter(p -> p.getEstado() == EstadoPedido.PRONTO || p.getEstado() == EstadoPedido.ENTREGUE)
    //             .collect(Collectors.toList());
        
    //     if (pedidosConcluidos.isEmpty()) {
    //         return 0.0;
    //     }
        
    //     long totalMinutos = 0;
    //     int contadorValidos = 0;
        
    //     for (Pedido pedido : pedidosConcluidos) {
    //         List<Tarefa> tarefas = tarefaDAO.findAllByPedido(pedido.getId());
            
    //         LocalDateTime inicio = tarefas.stream()
    //                 .map(Tarefa::getDataCriacao)
    //                 .min(LocalDateTime::compareTo)
    //                 .orElse(null);
            
    //         LocalDateTime fim = tarefas.stream()
    //                 .map(Tarefa::getDataConclusao)
    //                 .filter(d -> d != null)
    //                 .max(LocalDateTime::compareTo)
    //                 .orElse(null);
            
    //         if (inicio != null && fim != null) {
    //             Duration duracao = Duration.between(inicio, fim);
    //             totalMinutos += duracao.toMinutes();
    //             contadorValidos++;
    //         }
    //     }
        
    //     return contadorValidos > 0 ? (double) totalMinutos / contadorValidos : 0.0;
    // }

    // // --- Stock Consumption ---

    // @Override
    // public boolean verificarStockSuficiente(Integer restauranteId, Integer produtoId, int quantidade) {
    //     Restaurante restaurante = restauranteDAO.findById(restauranteId);
    //     if (restaurante == null) {
    //         return false;
    //     }
        
    //     Produto produto = produtoDAO.findById(produtoId);
    //     if (produto == null) {
    //         return false;
    //     }
        
    //     // Verificar cada ingrediente do produto
    //     for (LinhaProduto linhaProduto : produto.getLinhas()) {
    //         double quantidadeNecessaria = linhaProduto.getQuantidade() * quantidade;
            
    //         LinhaStock linhaStock = restaurante.getStock().stream()
    //                 .filter(ls -> ls.getIngredienteId() == linhaProduto.getIngredienteId())
    //                 .findFirst()
    //                 .orElse(null);
            
    //         if (linhaStock == null || linhaStock.getQuantidade() < quantidadeNecessaria) {
    //             return false;
    //         }
    //     }
        
    //     return true;
    // }

    // @Override
    // public void consumirIngredientes(Integer restauranteId, Integer produtoId, int quantidade) {
    //     Restaurante restaurante = restauranteDAO.findById(restauranteId);
    //     if (restaurante == null) {
    //         throw new IllegalArgumentException("Restaurante não encontrado: " + restauranteId);
    //     }
        
    //     Produto produto = produtoDAO.findById(produtoId);
    //     if (produto == null) {
    //         throw new IllegalArgumentException("Produto não encontrado: " + produtoId);
    //     }
        
    //     if (!verificarStockSuficiente(restauranteId, produtoId, quantidade)) {
    //         throw new IllegalStateException("Stock insuficiente para produzir o produto");
    //     }
        
    //     // Consumir ingredientes
    //     for (LinhaProduto linhaProduto : produto.getLinhas()) {
    //         double quantidadeConsumir = linhaProduto.getQuantidade() * quantidade;
            
    //         LinhaStock linhaStock = restaurante.getStock().stream()
    //                 .filter(ls -> ls.getIngredienteId() == linhaProduto.getIngredienteId())
    //                 .findFirst()
    //                 .orElse(null);
            
    //         if (linhaStock != null) {
    //             linhaStock.setQuantidade(linhaStock.getQuantidade() - quantidadeConsumir);
    //         }
    //     }
        
    //     restauranteDAO.update(restaurante);
    // }
}
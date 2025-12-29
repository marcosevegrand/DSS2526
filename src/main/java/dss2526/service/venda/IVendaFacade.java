package dss2526.service.venda;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;
import dss2526.domain.contract.Item;
import dss2526.service.base.IBaseFacade;
import java.time.Duration; // Import necessário
import java.util.List;

public interface IVendaFacade extends IBaseFacade {
    // Dados Base
    List<Restaurante> listarRestaurantes();
    List<Ingrediente> listarAlergenicosDisponiveis();
    
    // Fluxo de Pedido
    Pedido iniciarPedido(int restauranteId);
    List<Item> listarCatalogoFiltrado(int restauranteId, List<Integer> excluirAlergenicosIds);
    void adicionarItemAoPedido(int pedidoId, int itemId, TipoItem tipo, int qtd, String obs);
    void removerQuantidadeDoPedido(int pedidoId, int linhaId, int quantidade);
    void cancelarPedido(int pedidoId);
    
    // Pagamento e Monitorização
    // Alterado para retornar Duration (estimativa) ou null se for pagamento na caixa
    Duration processarPagamento(int pedidoId, TipoPagamento tipo);
    
    List<Pedido> listarPedidosAtivos(int restauranteId);
    Pedido obterPedido(int pedidoId);
    
    // Utilitários de UI
    String obterNomeItem(int itemId, TipoItem tipo);
}
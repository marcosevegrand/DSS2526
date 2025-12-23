package dss2526.venda;

import dss2526.domain.entity.Pedido;
import dss2526.domain.contract.Item;
import java.util.List;

/**
 * API mínima utilizada pela UI do terminal de vendas.
 * Implementações podem delegar para DAOs / serviços de domínio.
 */
public interface IVendaFacade {

    /**
     * Cria e persiste um novo pedido. Retorna o pedido criado.
     */
    Pedido criarPedido(boolean paraLevar);

    /**
     * Adiciona uma quantidade de um item (produto/menu) ao pedido.
     * Implementação concreta decide como mapear idItem para Produto/Menu e como persistir.
     */
    void adicionarItem(int idPedido, int idItem, int quantidade);

    /**
     * Remove uma quantidade de um item do pedido.
     */
    void removerItem(int idPedido, int idItem, int quantidade);

    /**
     * Adiciona uma nota/observação ao pedido.
     */
    void adicionarNota(int idPedido, String nota);

    /**
     * Marca o pedido como pago (ou inicia fluxo de pagamento).
     */
    void confirmarPedido(int idPedido);

    /**
     * Cancela o pedido.
     */
    void cancelarPedido(int idPedido);

    /**
     * Obtém o pedido (pode retornar null se não existir).
     */
    Pedido obterPedido(int idPedido);

    /**
     * Lista items disponíveis.
     */
    List<Item> obterItemsDisponiveis();
}
package pt.uminho.dss.restaurante.venda;

import java.util.List;

import pt.uminho.dss.restaurante.domain.entity.Pedido;
import pt.uminho.dss.restaurante.domain.entity.Produto;
import pt.uminho.dss.restaurante.domain.entity.Menu;

/**
 * API mínima utilizada pela UI do terminal de vendas.
 * Implementações podem delegar para DAOs / serviços de domínio.
 */
public interface IVenda {

    /**
     * Cria e persiste um novo pedido. Retorna o pedido criado.
     */
    Pedido criarPedido(Boolean takeaway);

    /**
     * Adiciona uma quantidade de um item (produto/menu) ao pedido.
     * Implementação concreta decide como mapear idItem para Produto/Menu e como persistir.
     */
    void adicionarItem(int idPedido, int idItem);

    /**
     * Remove uma quantidade de um item do pedido.
     */
    void removerItem(int idPedido, int idItem);

    /**
     * Marca o pedido como pago (ou inicia fluxo de pagamento).
     */
    void pagarPedido(int idPedido);

    /**
     * Cancela o pedido.
     */
    void cancelarPedido(int idPedido);

    /**
     * Obtém o pedido (pode retornar null se não existir).
     */
    Pedido obterPedido(int idPedido);

    /**
     * Lista produtos disponíveis.
     */
    List<Produto> listarProdutos();

    /**
     * Lista menus disponíveis.
     */
    List<Menu> listarMenus();
}
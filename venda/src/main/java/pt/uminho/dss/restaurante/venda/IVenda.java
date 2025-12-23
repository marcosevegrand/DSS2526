package pt.uminho.dss.restaurante.venda;

import java.util.List;

import pt.uminho.dss.restaurante.domain.entity.Pedido;
import pt.uminho.dss.restaurante.domain.entity.Produto;
import pt.uminho.dss.restaurante.domain.entity.Menu;
import pt.uminho.dss.restaurante.domain.enumeration.ModoConsumo;

/**
 * API mínima utilizada pela UI do terminal de vendas.
 * Implementações podem delegar para DAOs / serviços de domínio.
 */
public interface IVenda {

    /**
     * Cria e persiste um novo pedido. Retorna o pedido criado (pode ter id gerado).
     */
    Pedido criarPedido(ModoConsumo modo, int idTerminal, int idFuncionario);

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
     * Lista produtos disponíveis (catálogo).
     */
    List<Produto> listarProdutos();

    /**
     * Lista menus disponíveis.
     */
    List<Menu> listarMenus();
}
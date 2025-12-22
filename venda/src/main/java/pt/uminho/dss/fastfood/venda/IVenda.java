package pt.uminho.dss.fastfood.venda;

import pt.uminho.dss.fastfood.core.domain.Pedido;
import pt.uminho.dss.fastfood.core.domain.enums.ModoConsumo;
// Se tiveres DTOs, importa-os também daqui ou de um subpackage dto do módulo venda.

public interface IVenda {

    // Ciclo de vida do pedido
    Pedido iniciarPedido(ModoConsumo modoConsumo, int idTerminal, int idFuncionario);

    Pedido adicionarItem(int idPedido,
                         int idProdutoOuMenu,
                         String personalizacao, // ou um DTO próprio
                         int quantidade);

    Pedido removerItem(int idPedido, int idLinhaPedido);

    Pedido editarItem(int idPedido,
                      int idLinhaPedido,
                      String novaPersonalizacao,
                      int novaQuantidade);

    Pedido cancelarPedido(int idPedido);

    Pedido confirmarPedido(int idPedido);

    // Consulta
    Pedido obterPedido(int idPedido);
}

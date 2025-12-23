package pt.uminho.dss.restaurante.venda;

import pt.uminho.dss.restaurante.core.domain.entity.Pedido;
import pt.uminho.dss.restaurante.core.domain.enumeration.ModoConsumo;

// Se tiveres DTOs, importa-os também daqui ou de um subpackage dto do módulo venda.

public interface IVenda {
    // Ciclo de vida do pedido
    Pedido iniciarPedido();

    Pedido adicionarItem(int idItem, int quantidade);

    Pedido removerItem(int idPedido, int idLinhaPedido);

    Pedido editarItem(
        int idPedido,
        int idLinhaPedido,
        String novaPersonalizacao,
        int novaQuantidadeidLinhaPedido
    );

    Pedido cancelarPedido(int idPedido);

    Pedido confirmarPedido(int idPedido);

    Pedido obterPedido(int idPedido);
}

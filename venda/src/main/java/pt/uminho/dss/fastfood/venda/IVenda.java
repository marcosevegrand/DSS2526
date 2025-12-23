package pt.uminho.dss.fastfood.venda;

import pt.uminho.dss.fastfood.core.domain.entity.Pedido;
import pt.uminho.dss.fastfood.core.domain.entity.Talao;
import pt.uminho.dss.fastfood.core.domain.enumeration.ModoConsumo;
// Se tiveres DTOs, importa-os também daqui ou de um subpackage dto do módulo venda.

public interface IVenda {

    Pedido iniciarPedido(ModoConsumo modoConsumo, int idTerminal, int idFuncionario);

    Pedido adicionarItem(int idPedido, int idProdutoOuMenu, String personalizacao, int quantidade);

    Pedido removerItem(int idPedido, int idLinhaPedido);

    Pedido editarItem(int idPedido, int idLinhaPedido, String novaPersonalizacao, int novaQuantidade);

    Pedido confirmarPedido(int idPedido);

    void cancelarPedido(int idPedido);

    // pagamento simplificado
    Pedido marcarComoPagoNaCaixa(int idPedido);        // usado pelo módulo da caixa
    Pedido marcarComoPagoNoTerminal(int idPedido);     // usado quando o cliente escolhe multibanco

    Talao emitirTalao(int idPedido);                   // só deve aceitar pedidos pagos
}

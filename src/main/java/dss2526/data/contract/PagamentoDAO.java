package dss2526.data.contract;

import dss2526.domain.entity.Pagamento;

public interface PagamentoDAO extends GenericDAO<Pagamento, Integer> {
    Pagamento findByPedido(int pedidoId);
}

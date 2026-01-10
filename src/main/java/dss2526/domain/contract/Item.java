package dss2526.domain.contract;

/**
 * Interface comum para itens vendáveis (Produto ou Menu).
 */
public interface Item {
    int getId();
    String getNome();
    double getPreco();
}
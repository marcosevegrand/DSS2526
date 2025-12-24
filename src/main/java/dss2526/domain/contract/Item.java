package dss2526.domain.contract;

/**
 * Interface representativa de um item comercializável.
 * Mantida sem anotações para garantir a pureza do domínio.
 */
public interface Item {

    int getId();
    String getNome();
    double getPreco();
}
package dss2526.domain.contract;

import java.math.BigDecimal;

/**
 * Interface representativa de um item comercializável.
 * Mantida sem anotações para garantir a pureza do domínio.
 */
public interface Item {

    BigDecimal getPreco();

    boolean isDisponivel();

    /**
     * Valida se o preço do item é não-negativo.
     */
    default void validarPreco() {
        if (getPreco() == null || getPreco().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O preço do item deve ser não-negativo.");
        }
    }

    /**
     * Valida se o item está disponível antes de ser adicionado a um pedido.
     */
    default void validarDisponibilidade() {
        if (!isDisponivel()) {
            throw new IllegalStateException("O item não está disponível para ser adicionado ao pedido.");
        }
    }
}
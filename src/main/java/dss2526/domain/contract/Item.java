package dss2526.domain.contract;

import java.math.BigDecimal;

/**
 * Interface representativa de um item comercializável.
 * Mantida sem anotações para garantir a pureza do domínio.
 */
public interface Item {

    public BigDecimal getPreco();
    
    public boolean isDisponivel();
}
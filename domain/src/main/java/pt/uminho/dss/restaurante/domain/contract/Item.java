package pt.uminho.dss.restaurante.domain.contract;

import java.math.BigDecimal;

/**
 * Interface representativa de um item comercializável.
 * Mantida sem anotações para garantir a pureza do domínio.
 */
public interface Item {

    public BigDecimal getPreco();

    public boolean isDisponivel();

    public Integer getId();

    public String getNome();
}
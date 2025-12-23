package pt.uminho.dss.fastfood.core.domain.contract;

import pt.uminho.dss.fastfood.core.domain.enumeration.TipoItem;

public interface Item {

    int getId();

    String getNome();

    float calcularPreco(int quantidade, String personalizacao);

    int calcularTempoPreparacao(int quantidade, String personalizacao);

}


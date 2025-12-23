package pt.uminho.dss.restaurante.core.domain.contract;

public interface Item {
    int getId();

    String getNome();

    float calcularPreco(int quantidade, String personalizacao);

    int calcularTempoPreparacao(int quantidade, String personalizacao);
}

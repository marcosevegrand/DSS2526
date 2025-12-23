package pt.uminho.dss.restaurante.domain.contract;

public interface Item {
    int getId();

    String getNome();

    float getPreco();

    int getTempoPreparacao();
}

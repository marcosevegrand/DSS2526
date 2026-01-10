package dss2526.domain.entity;

import java.time.LocalDateTime;

public class Mensagem {
    private int id;
    private int restauranteId;
    private String texto;
    private LocalDateTime dataHora;

    public Mensagem() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getRestauranteId() { return restauranteId; }
    public void setRestauranteId(int restauranteId) { this.restauranteId = restauranteId; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
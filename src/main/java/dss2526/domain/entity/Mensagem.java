package dss2526.domain.entity;

import java.time.LocalDateTime;

public class Mensagem {
    private int id;
    private String texto;
    private LocalDateTime dataHora;
    private boolean urgente;

    // Construtores

    public Mensagem() {
        this.dataHora = LocalDateTime.now();
    }

    public Mensagem(String texto, boolean urgente) {
        this();
        this.texto = texto;
        this.urgente = urgente;
    }

    public Mensagem(String texto, LocalDateTime dataHora, boolean urgente) {
        this(texto, false);
    }

    // Getters e Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public boolean isUrgente() { return urgente; }
    public void setUrgente(boolean urgente) { this.urgente = urgente; }
    
}
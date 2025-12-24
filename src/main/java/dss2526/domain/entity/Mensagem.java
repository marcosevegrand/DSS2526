package dss2526.domain.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Mensagem {
    private final String texto;
    private final LocalDateTime dataHora;
    private final boolean urgente;

    public Mensagem(String texto, boolean urgente) {
        this.texto = texto;
        this.dataHora = LocalDateTime.now();
        this.urgente = urgente;
    }

    // Getters
    public String getTexto() { return texto; }
    public boolean isUrgente() { return urgente; }
    
    // Método útil para a UI de Produção mostrar a hora do aviso
    public String getHoraFormatada() {
        return dataHora.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    @Override
    public String toString() {
        String prefixo = urgente ? "[URGENTE] " : "[AVISO] ";
        return prefixo + "(" + getHoraFormatada() + "): " + texto;
    }
}
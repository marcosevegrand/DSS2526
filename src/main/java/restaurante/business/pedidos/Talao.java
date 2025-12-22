package restaurante.business.pedidos;

import java.util.Date;

/**
 * Represents a receipt/ticket for an order
 */
public class Talao {
    private int id;
    private Date data;
    private int tempoEspera; // in minutes
    private String conteudo;
    private int numero;
    
    public Talao(int id, int numero) {
        this.id = id;
        this.numero = numero;
        this.data = new Date();
        this.conteudo = "";
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Date getData() {
        return data;
    }
    
    public void setData(Date data) {
        this.data = data;
    }
    
    public int getTempoEspera() {
        return tempoEspera;
    }
    
    public void setTempoEspera(int tempoEspera) {
        this.tempoEspera = tempoEspera;
    }
    
    public String getConteudo() {
        return conteudo;
    }
    
    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
    
    public int getNumero() {
        return numero;
    }
    
    public void setNumero(int numero) {
        this.numero = numero;
    }
    
    public void gerarTalao(Pedido pedido) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== TALAO #").append(numero).append(" ===\n");
        sb.append("Data: ").append(data).append("\n");
        sb.append("Pedido ID: ").append(pedido.getId()).append("\n");
        sb.append("Tempo Espera: ").append(tempoEspera).append(" min\n");
        sb.append("====================\n");
        this.conteudo = sb.toString();
    }
}

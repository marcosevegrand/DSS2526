package dss2526.ui.delegate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Versao modificada da classe fornecida pelo professor José Creissac Campos
 */
public class NewMenu {

    // Interfaces auxiliares

    /** Functional interface para handlers com controlo de saída. */
    public interface Handler {
        /**
         * Executa o handler.
         * @return true para sair do menu após executar, false para continuar no menu
         */
        boolean execute();
    }

    /** Functional interface para handlers legados (sem retorno). */
    @FunctionalInterface
    public interface LegacyHandler {
        void execute();
    }

    /** Functional interface para pré-condições. */
    public interface PreCondition {  
        boolean validate();
    }

    // Variável de classe para suportar leitura

    private static Scanner is = new Scanner(System.in);

    // Variáveis de instância

    private String title;                   // Título do NewMenu
    private List<String> opcoes;            // Lista de opções
    private List<PreCondition> disponivel;  // Lista de pré-condições
    private List<Handler> handlers;         // Lista de handlers

    // Construtor

    /**
     * Constructor for objects of class NewMenu
     */
    public NewMenu(String title, String[] opcoes) {
        this.title = title;
        this.opcoes = Arrays.asList(opcoes);
        this.disponivel = new ArrayList<>();
        this.handlers = new ArrayList<>();
        this.opcoes.forEach(s-> {
            this.disponivel.add(() -> true);
            this.handlers.add(() -> {
                System.out.println("\nChoice not implemented!");
                return false; // Por omissão, não sai do menu
            });
        });
    }

    // Métodos de instância

    /**
     * Correr o NewMenu.
     *
     * Termina com a opção 0 (zero) ou se um handler retornar true.
     */
    public void run() {
        int op;
        boolean exit = false;
        do {
            show();
            op = readOption();
            if (op > 0 && !this.disponivel.get(op - 1).validate()) {
                System.out.println("\nChoice not available! Try again.");
            } else if (op > 0) {
                // Executar handler e verificar se deve sair
                exit = this.handlers.get(op - 1).execute();
            }
        } while (op != 0 && !exit);
    }

    /**
     * Método que regista uma pré-condição numa opção do NewMenu.
     *
     * @param i índice da opção (começa em 1)
     * @param b pré-condição a registar
     */
    public void setPreCondition(int i, PreCondition b) {
        this.disponivel.set(i - 1, b);
    }

    /**
     * Método para registar um handler com controlo de saída numa opção do NewMenu.
     *
     * @param i indice da opção  (começa em 1)
     * @param h handler a registar
     */
    public void setHandler(int i, Handler h) {
        this.handlers.set(i - 1, h);
    }

    /**
     * Método para registar um handler legado (sem retorno) numa opção do NewMenu.
     * O menu não será fechado após a execução deste handler.
     *
     * @param i indice da opção  (começa em 1)
     * @param h handler legado a registar
     */
    public void setHandler(int i, LegacyHandler h) {
        this.handlers.set(i - 1, () -> {
            h.execute();
            return false; // Por omissão, não sai do menu
        });
    }

    // Métodos auxiliares

    /** Apresentar o NewMenu */
    private void show() {
        System.out.println("\n===== " + this.title + " =====");
        for (int i = 0; i < this.opcoes.size(); i++) {
            System.out.print(i + 1);
            System.out.print(". ");
            System.out.println(this.disponivel.get(i).validate() ? this.opcoes.get(i) : "---");
        }
        System.out.println("0. Sair");
    }

    /** Ler uma opção válida */
    private int readOption() {
        int op;
        System.out.print("Enter your choice: ");
        try {
            if (!is.hasNextLine()) {
                System.out.println("\nNo input detected. Exiting menu.");
                return 0; // Exit gracefully
            }
            String line = is.nextLine();
            op = Integer.parseInt(line);
        } catch (NumberFormatException e) { // Not an int
            op = -1;
        }
        if (op < 0 || op > this.opcoes.size()) {
            System.out.println("\nInvalid choice. Try again.");
            op = -1;
        }
        return op;
    }
}
package dss2526.ui.view;

import dss2526.ui.controller.GestaoController;
import dss2526.ui.util.NewMenu;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class GestaoUI {
    private final GestaoController controller;
    private final Scanner sc;

    public GestaoUI(GestaoController controller) {
        this.controller = controller;
        this.sc = new Scanner(System.in);
    }

    public void show() {
        System.out.println("PORTAL DE GESTAO");
        System.out.print("Utilizador: "); String u = sc.nextLine();
        System.out.print("Password: "); String p = sc.nextLine();

        if (controller.autenticar(u, p)) {
            if (controller.ehCOO()) menuCOO();
            else menuGerente();
        } else {
            System.out.println("Login falhou.");
        }
    }

    private void menuCOO() {
        NewMenu menu = new NewMenu("PAINEL COO", new String[]{
            "Listar Restaurantes",
            "Selecionar Restaurante para Gestao Local",
            "Criar Novo Restaurante"
        });
        menu.setHandler(1, () -> { controller.listarRestaurantes().forEach(System.out::println); return false; });
        menu.setHandler(2, () -> { 
            int idx = escolher("Selecione o Restaurante", controller.listarRestaurantes());
            controller.selecionarRestaurante(idx);
            menuGerente();
            return false; 
        });
        menu.run();
    }

    private void menuGerente() {
        NewMenu menu = new NewMenu("GESTAO DE UNIDADE", new String[]{
            "Gerir Equipa (Contratar/Demitir)",
            "Atualizar Stock",
            "Consultar Estatisticas"
        });
        
        menu.setHandler(1, () -> { fluxosEquipa(); return false; });
        menu.setHandler(2, () -> { fluxosStock(); return false; });
        menu.setHandler(3, () -> { fluxosEstatisticas(); return false; });
        menu.run();
    }

    private void fluxosEquipa() {
        System.out.println("1. Listar/Demitir | 2. Contratar");
        String op = sc.nextLine();
        if (op.equals("1")) {
            List<String> funcs = controller.listarFuncionarios();
            if (funcs.isEmpty()) return;
            int idx = escolher("Selecione para demitir (ou 0 cancelar)", funcs);
            controller.demitir(idx);
        } else {
            System.out.print("Username: "); String u = sc.nextLine();
            System.out.print("Password: "); String p = sc.nextLine();
            System.out.print("Cargo (1-Func, 2-Gerente): "); int c = Integer.parseInt(sc.nextLine());
            controller.contratar(u, p, c);
        }
    }

    private void fluxosStock() {
        List<String> ings = controller.listarIngredientes();
        int idx = escolher("Ingrediente", ings);
        System.out.print("Quantidade a adicionar: ");
        int q = Integer.parseInt(sc.nextLine());
        controller.atualizarStock(idx, q);
    }

    private void fluxosEstatisticas() {
        System.out.println("Filtro Temporal (Formato: yyyy-MM-dd HH:mm)");
        System.out.println("Deixe em branco para 'Desde o inicio'");
        
        LocalDateTime inicio = lerData("Data Inicio: ");
        LocalDateTime fim = lerData("Data Fim: ");
        
        System.out.println("\n--- RESULTADOS ---");
        System.out.println(controller.obterDadosEstatisticos(inicio, fim));
        System.out.println("Pressione Enter para continuar...");
        sc.nextLine();
    }

    private LocalDateTime lerData(String msg) {
        System.out.print(msg);
        String s = sc.nextLine();
        if (s.isBlank()) return null;
        try {
            return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (Exception e) {
            System.out.println("Formato invalido. Ignorando filtro.");
            return null;
        }
    }

    private int escolher(String t, List<String> ops) {
        System.out.println("\n" + t);
        for (int i = 0; i < ops.size(); i++) System.out.println((i + 1) + ". " + ops.get(i));
        System.out.print("Seleccao: ");
        return Integer.parseInt(sc.nextLine()) - 1;
    }
}
package dss2526.ui.view;

import dss2526.ui.controller.GestaoController;
import dss2526.ui.util.NewMenu;
import dss2526.domain.enumeration.Funcao;
import java.util.*;

public class GestaoUI {
    private final GestaoController c = new GestaoController();
    private final Scanner sc = new Scanner(System.in);

    public void run() {
        System.out.print("Utilizador: "); String u = sc.nextLine();
        System.out.print("Senha: "); String p = sc.nextLine();
        if(!c.autenticar(u, p)) { System.out.println("Acesso negado."); return; }

        NewMenu.builder("PAINEL DE GESTÃO")
            .style(NewMenu.MenuStyle.NUMBERED)
            .addOption("Recursos Humanos (Contratar/Demitir)", () -> {
                System.out.println("1. Contratar | 2. Demitir");
                String op = sc.nextLine();
                if(op.equals("1")) {
                    System.out.print("User: "); String nu = sc.nextLine();
                    c.contratar(nu, "123", Funcao.FUNCIONARIO, c.getRestauranteProprio());
                } else if(op.equals("2")) {
                    System.out.print("ID Funcionário: ");
                    c.demitir(Integer.parseInt(sc.nextLine()));
                }
                return false;
            })
            .addOption("Gestão de Inventário", () -> {
                int rid = c.isCOO() ? c.getRestauranteId(pick("Restaurante", c.getRestaurantes())) : c.getRestauranteProprio();
                int idx = pick("Ingrediente", c.getIngredientes());
                System.out.print("Quantidade (+/-): ");
                c.stock(rid, idx, Integer.parseInt(sc.nextLine()));
                return false;
            })
            .addOption("Gestão de Estações", () -> {
                int rid = c.isCOO() ? c.getRestauranteId(pick("Restaurante", c.getRestaurantes())) : c.getRestauranteProprio();
                System.out.println("1. Adicionar | 2. Remover");
                if(sc.nextLine().equals("1")) {
                    System.out.print("Nome: "); String name = sc.nextLine();
                    System.out.print("É Caixa? (s/n): ");
                    c.addEstacao(rid, name, sc.nextLine().equalsIgnoreCase("s"));
                } else {
                    System.out.print("ID Estação: ");
                    c.remEstacao(Integer.parseInt(sc.nextLine()));
                }
                return false;
            })
            .addOption("Dashboard de Estatísticas", () -> {
                int rid = c.isCOO() ? c.getRestauranteId(pick("Restaurante", c.getRestaurantes())) : c.getRestauranteProprio();
                System.out.println("\n" + c.stats(rid, null, null));
                return false;
            })
            .addOption("Enviar Comunicação", () -> {
                System.out.println("1. Mensagem Local | 2. Mensagem Global (COO)");
                String op = sc.nextLine();
                System.out.print("Texto: "); String t = sc.nextLine();
                if(op.equals("1")) c.msgRest(c.isCOO() ? c.getRestauranteId(pick("Restaurante", c.getRestaurantes())) : c.getRestauranteProprio(), t);
                else if(c.isCOO()) c.msgGlobal(t);
                return false;
            })
            .run();
    }

    private int pick(String t, List<String> l) {
        for(int i=0; i<l.size(); i++) System.out.println((i+1)+". "+l.get(i));
        System.out.print(t + " > ");
        try { return Integer.parseInt(sc.nextLine())-1; } catch(Exception e) { return 0; }
    }
}
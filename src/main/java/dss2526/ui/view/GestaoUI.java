package dss2526.ui.view;

import dss2526.ui.controller.GestaoController;
import dss2526.ui.util.NewMenu;
import dss2526.domain.entity.Funcionario;
import dss2526.domain.entity.Restaurante;
import dss2526.domain.enumeration.Funcao;
import dss2526.domain.enumeration.Trabalho;

import java.util.List;
import java.util.Scanner;

public class GestaoUI {
    private final GestaoController controller;
    private final Scanner scanner;

    public GestaoUI(GestaoController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public void show() {
        System.out.println("\n*** Portal de Gestão ***");
        
        while (true) {
            System.out.println("\n--- Autenticação ---");
            String user = lerString("Utilizador: ");
            if (user.isEmpty()) return; 
            
            String pass = lerString("Password: ");

            // Sincronizado com o método 'login' do GestaoController
            if (controller.login(user, pass)) {
                System.out.println("Login efetuado com sucesso. Perfil: " + controller.getCargoUtilizador());
                menuPrincipal();
                // Opcional: controller.encerrarSessao() se tiveres esse método
            } else {
                System.out.println("Credenciais inválidas.");
            }
        }
    }

    private void menuPrincipal() {
        if (controller.ehCOO()) {
            menuAdminCOO();
        } else {
            menuOperacoesRestaurante("Gestão da Unidade");
        }
    }

    private void menuAdminCOO() {
        NewMenu menu = new NewMenu("--- Painel Administrador (COO) ---", new String[]{
            "Listar Todos os Restaurantes",
            "Criar Novo Restaurante",
            "Gerir Unidade Específica",
            "Sair"
        });

        menu.setHandler(1, () -> {
            List<Restaurante> rests = controller.getTodosRestaurantes();
            if (rests.isEmpty()) System.out.println("Não existem restaurantes.");
            else rests.forEach(r -> System.out.println(r.getId() + " - " + r.getNome()));
        });

        menu.setHandler(2, () -> {
            System.out.println("Funcionalidade delegada ao Administrador de Sistemas.");
            // Aqui chamarias controller.criarRestaurante se o tivesses implementado
        });

        menu.setHandler(3, () -> {
            List<Restaurante> rests = controller.getTodosRestaurantes();
            Integer id = escolherRestaurante(rests);
            if (id != null) {
                controller.selecionarRestaurante(id);
                menuOperacoesRestaurante("Administrando Restaurante #" + id);
            }
        });
        
        menu.setHandler(4, () -> true);

        menu.run();
    }

    private void menuOperacoesRestaurante(String titulo) {
        int rId = controller.getRestauranteAtivoId();
        if (rId == -1) {
            System.out.println("Erro: Nenhum restaurante ativo.");
            return;
        }

        NewMenu menu = new NewMenu("--- " + titulo + " (ID: " + rId + ") ---", new String[]{
            "Ver Equipa",
            "Contratar Funcionário",
            "Consultar Faturação",
            "Enviar Aviso à Cozinha",
            "Atualizar Stock",
            "Voltar"
        });

        menu.setHandler(1, () -> {
            List<Funcionario> equipa = controller.getEquipa();
            if (equipa.isEmpty()) System.out.println("Sem funcionários.");
            else equipa.forEach(f -> System.out.println(f.getUtilizador() + " [" + f.getFuncao() + "]"));
        });

        menu.setHandler(2, () -> {
            String user = lerString("Username: ");
            String pass = lerString("Password: ");
            Funcionario novo = new Funcionario();
            novo.setUtilizador(user);
            novo.setPassword(pass);
            novo.setFuncao(Funcao.FUNCIONARIO);
            novo.setRestauranteId(rId);
            
            controller.contratar(novo);
            System.out.println("Funcionário registado.");
        });

        menu.setHandler(3, () -> {
            System.out.printf("Faturação total: %.2f €%n", controller.getFaturacao());
        });

        menu.setHandler(4, () -> {
            String msg = lerString("Mensagem: ");
            boolean urgente = lerString("Urgente? (s/n): ").equalsIgnoreCase("s");
            controller.enviarMensagem(msg, urgente);
            System.out.println("Mensagem enviada para a fila da cozinha.");
        });

        menu.setHandler(5, () -> {
            int ingId = lerInt("ID Ingrediente: ");
            float qtd = (float) lerDouble("Quantidade: ");
            controller.atualizarStock(ingId, qtd);
        });

        menu.setHandler(6, () -> true);

        menu.run();
    }

    // --- Helpers ---

    private Integer escolherRestaurante(List<Restaurante> lista) {
        if (lista.isEmpty()) return null;
        for (int i = 0; i < lista.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, lista.get(i).getNome());
        }
        int escolha = lerInt("Escolha (0 para cancelar): ");
        if (escolha <= 0 || escolha > lista.size()) return null;
        return lista.get(escolha - 1).getId();
    }

    private String lerString(String msg) { System.out.print(msg); return scanner.nextLine(); }
    private int lerInt(String msg) { System.out.print(msg); try { return Integer.parseInt(scanner.nextLine()); } catch (Exception e) { return 0; } }
    private double lerDouble(String msg) { System.out.print(msg); try { return Double.parseDouble(scanner.nextLine()); } catch (Exception e) { return 0; } }
}
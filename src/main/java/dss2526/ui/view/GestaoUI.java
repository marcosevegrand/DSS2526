package dss2526.ui.view;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.Funcao;
import dss2526.domain.enumeration.Trabalho;
import dss2526.ui.controller.GestaoController;
import dss2526.ui.util.NewMenu;

import java.util.ArrayList;
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
            System.out.println("\n🔐 Autenticação Necessária (Deixe vazio e Enter para Sair)");
            String user = lerString("Utilizador: ");
            if (user.isEmpty()) {
                System.out.println("A encerrar o sistema de gestão...");
                break; // Encerra o loop e sai
            }
            
            String pass = lerString("Password: ");

            if (controller.login(user, pass)) {
                System.out.println("✅ Bem-vindo, " + controller.getNomeUtilizador());
                menuPrincipal();
                controller.logout(); 
                // Loop continua para permitir novo login, mas utilizador pode sair com Enter vazio
            } else {
                System.out.println("❌ Credenciais inválidas.");
            }
        }
    }

    private void menuPrincipal() {
        if (controller.isCOO()) menuCOO();
        else if (controller.isGerente()) menuGerente();
        else menuFuncionario();
    }

    // --- MENUS POR PERFIL ---

    private void menuCOO() {
        NewMenu menu = new NewMenu("PAINEL ADMINISTRAÇÃO (COO)", new String[]{
            "🏢 Gerir Restaurantes",
            "🌍 Gestão Global (Catálogos/Produtos/Passos)",
            "📊 Aceder a Unidade Específica"
        });
        menu.setHandler(1, () -> { menuRestaurantes(); return false; });
        menu.setHandler(2, () -> { menuGlobal(); return false; });
        menu.setHandler(3, () -> { 
            selecionarContextoRestaurante(); 
            if (!controller.getNomeRestauranteAtivo().equals("Nenhum")) menuGerente();
            return false; 
        });
        menu.run();
    }

    private void menuGerente() {
        String titulo = "GESTÃO LOCAL: " + controller.getNomeRestauranteAtivo();
        NewMenu menu = new NewMenu(titulo, new String[]{
            "👥 Gerir Equipa",
            "🏭 Gerir Estações",
            "📦 Atualizar Stock",
            "📜 Alterar Catálogo Ativo",
            "📈 Dashboard & Estatísticas",
            "📢 Enviar Aviso à Cozinha"
        });
        menu.setHandler(1, () -> { menuEquipa(); return false; });
        menu.setHandler(2, () -> { menuEstacoes(); return false; });
        menu.setHandler(3, () -> { fluxoStock(); return false; });
        menu.setHandler(4, () -> { fluxoAlterarCatalogo(); return false; });
        menu.setHandler(5, () -> { mostrarEstatisticas(); esperarEnter(); return false; });
        menu.setHandler(6, () -> { fluxoMensagem(); return false; });
        menu.run();
    }

    private void menuFuncionario() {
        String titulo = "ÁREA FUNCIONÁRIO: " + controller.getNomeRestauranteAtivo();
        NewMenu menu = new NewMenu(titulo, new String[]{
            "📦 Registar Entrada de Stock",
            "📢 Enviar Aviso à Cozinha"
        });
        menu.setHandler(1, () -> { fluxoStock(); return false; });
        menu.setHandler(2, () -> { fluxoMensagem(); return false; });
        menu.run();
    }

    // --- SUB-MENUS GLOBAIS (COO) ---

    private void menuRestaurantes() {
        NewMenu menu = new NewMenu("GESTÃO DE RESTAURANTES", new String[]{ "Listar Todos", "Criar Novo Restaurante" });
        menu.setHandler(1, () -> { controller.listarRestaurantes().forEach(System.out::println); esperarEnter(); return false; });
        menu.setHandler(2, () -> {
            String nome = lerString("Nome do Restaurante: ");
            String local = lerString("Localização: ");
            try { controller.criarRestaurante(nome, local); System.out.println("✅ Restaurante criado."); } 
            catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
            return false;
        });
        menu.run();
    }

    private void menuGlobal() {
        NewMenu menu = new NewMenu("GESTÃO GLOBAL DE DADOS", new String[]{
            "Criar Ingrediente",
            "Criar Passo",
            "Criar Produto",
            "Criar Menu",
            "Criar Catálogo"
        });
        
        menu.setHandler(1, () -> {
            String nome = lerString("Nome: ");
            String uni = lerString("Unidade (kg/L/un): ");
            String alerg = lerString("Alergénico (ENTER se nulo): ");
            controller.criarIngrediente(nome, uni, alerg);
            System.out.println("✅ Ingrediente registado.");
            return false;
        });

        menu.setHandler(2, () -> { // Criar Passo
            String nome = lerString("Nome do Passo: ");
            long duracao = lerInt("Duração (minutos): ");
            System.out.println("Tipos: GRELHA, FRITURA, MONTAGEM, BEBIDAS, GELADOS, CAIXA");
            Trabalho trab = Trabalho.valueOf(lerString("Trabalho: ").toUpperCase());
            
            // Seleção Múltipla de Ingredientes
            List<Ingrediente> todosIng = controller.listarTodosIngredientes();
            List<Integer> selecionados = selecionarMultiplos(todosIng, Ingrediente::getNome, "Ingredientes Usados");
            
            controller.criarPasso(nome, duracao, trab, selecionados);
            System.out.println("✅ Passo registado.");
            return false;
        });
        
        menu.setHandler(3, () -> { // Criar Produto
            String nome = lerString("Nome Produto: ");
            double preco = lerDouble("Preço: ");
            
            // Receita: Selecionar Ingredientes e quantidades
            List<Ingrediente> todosIng = controller.listarTodosIngredientes();
            List<Integer> ingIds = new ArrayList<>();
            List<Integer> quantidades = new ArrayList<>();
            
            System.out.println("\n--- Definir Receita (Ingredientes) ---");
            while(true) {
                Integer idx = escolherItem(todosIng, Ingrediente::getNome, "Adicionar Ingrediente à receita");
                if (idx == null) break;
                int qtd = lerInt("Quantidade necessária: ");
                ingIds.add(todosIng.get(idx).getId());
                quantidades.add(qtd);
                System.out.println("Adicionado.");
            }

            // Workflow: Selecionar Passos
            List<Passo> todosPassos = controller.listarTodosPassos();
            List<Integer> passosIds = selecionarMultiplos(todosPassos, Passo::getNome, "Passos de Preparação");
            
            controller.criarProduto(nome, preco, passosIds, ingIds, quantidades);
            System.out.println("✅ Produto registado.");
            return false;
        });

        menu.setHandler(4, () -> { // Criar Menu
            String nome = lerString("Nome do Menu: ");
            double preco = lerDouble("Preço do Menu: ");
            
            List<Produto> todosProd = controller.listarTodosProdutos();
            List<Integer> prodIds = selecionarMultiplos(todosProd, Produto::getNome, "Produtos do Menu");
            
            controller.criarMenu(nome, preco, prodIds);
            System.out.println("✅ Menu registado.");
            return false;
        });

        menu.setHandler(5, () -> { // Criar Catálogo
            String nome = lerString("Nome do Catálogo: ");
            controller.criarCatalogo(nome);
            System.out.println("✅ Catálogo criado (vazio).");
            return false;
        });
        
        menu.run();
    }

    // --- GESTÃO LOCAL ---

    private void menuEquipa() {
        NewMenu menu = new NewMenu("GESTÃO DE EQUIPA", new String[]{ "Listar Funcionários", "Contratar", "Demitir" });
        menu.setHandler(1, () -> { controller.listarFuncionariosLocais().forEach(System.out::println); esperarEnter(); return false; });
        menu.setHandler(2, () -> {
            String u = lerString("Username: ");
            String p = lerString("Password: ");
            System.out.println("Cargos: 1-FUNCIONARIO, 2-GERENTE, 3-COO");
            int c = lerInt("Cargo: ");
            Funcao f = (c == 2) ? Funcao.GERENTE : (c == 3) ? Funcao.COO : Funcao.FUNCIONARIO;
            try { controller.contratarFuncionario(u, p, f); System.out.println("✅ Contratado."); } 
            catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
            return false;
        });
        menu.setHandler(3, () -> {
            int id = lerInt("ID do funcionário a demitir: ");
            try { controller.demitirFuncionario(id); System.out.println("⚠️ Funcionário removido."); } 
            catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
            return false;
        });
        menu.run();
    }

    private void menuEstacoes() {
        NewMenu menu = new NewMenu("CONFIGURAÇÃO DA COZINHA", new String[]{ "Listar Estações", "Adicionar Estação" });
        menu.setHandler(1, () -> { controller.listarEstacoesLocais().forEach(System.out::println); esperarEnter(); return false; });
        menu.setHandler(2, () -> {
            System.out.println("Tipos: GRELHA, FRITURA, MONTAGEM, BEBIDAS, GELADOS, CAIXA");
            String tipoStr = lerString("Tipo de Trabalho: ").toUpperCase();
            try { controller.adicionarEstacao(Trabalho.valueOf(tipoStr)); System.out.println("✅ Estação adicionada."); } 
            catch (Exception e) { System.out.println("❌ Inválido."); }
            return false;
        });
        menu.run();
    }

    private void fluxoStock() {
        int idIng = lerInt("ID do Ingrediente: ");
        int qtd = lerInt("Quantidade a adicionar (ou negativo para remover): ");
        try { controller.atualizarStock(idIng, qtd); System.out.println("✅ Stock atualizado."); } 
        catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }

    private void fluxoAlterarCatalogo() {
        List<Catalogo> cats = controller.listarTodosCatalogos();
        Integer idx = escolherItem(cats, Catalogo::getNome, "Escolha o novo Catálogo");
        if (idx != null) {
            try {
                controller.mudarCatalogoRestaurante(cats.get(idx).getId());
                System.out.println("✅ Catálogo do restaurante atualizado com sucesso.");
            } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
        }
    }

    private void fluxoMensagem() {
        String msg = lerString("Mensagem: ");
        boolean urg = lerString("Urgente? (s/n): ").equalsIgnoreCase("s");
        controller.enviarMensagem(msg, urg);
        System.out.println("✅ Enviado.");
    }

    private void mostrarEstatisticas() {
        System.out.println("\n📊 --- DASHBOARD ESTATÍSTICO --- 📊");
        
        System.out.println("\n💰 FINANCEIRO:");
        System.out.println(controller.getRelatorioFinanceiro());
        
        System.out.println("\n📦 VOLUME DE PEDIDOS:");
        controller.getRelatorioVolumePedidos().forEach(line -> System.out.println("  " + line));

        System.out.println("\n⏱️ PERFORMANCE:");
        System.out.println("  " + controller.getTempoMedioEspera());

        System.out.println("\n🏆 TOP PRODUTOS:");
        controller.getTopProdutos().forEach(line -> System.out.println("  " + line));

        System.out.println("\n🔥 CARGA NAS ESTAÇÕES (Tarefas):");
        controller.getCargaEstacoes().forEach(line -> System.out.println("  " + line));

        System.out.println("\n⚠️ ALERTAS DE STOCK (Critico < 20):");
        List<String> alertas = controller.getAlertasStock(20);
        if (alertas.isEmpty()) System.out.println("  ✅ Stock Saudável");
        else alertas.forEach(line -> System.out.println("  " + line));
    }

    // --- Helpers Genéricos ---

    private <T> Integer escolherItem(List<T> lista, java.util.function.Function<T, String> nomeMapper, String titulo) {
        if (lista.isEmpty()) { System.out.println("Lista vazia."); return null; }
        System.out.println("\n>>> " + titulo + " <<<");
        for (int i = 0; i < lista.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, nomeMapper.apply(lista.get(i)));
        }
        int op = lerInt("Escolha (0 para cancelar/terminar): ");
        if (op <= 0 || op > lista.size()) return null;
        return op - 1;
    }

    private <T> List<Integer> selecionarMultiplos(List<T> lista, java.util.function.Function<T, String> nomeMapper, String titulo) {
        List<Integer> selecionadosIds = new ArrayList<>();
        
        while (true) {
            Integer idx = escolherItem(lista, nomeMapper, titulo + " (Adicionar mais)");
            if (idx == null) break;
            
            T item = lista.get(idx);
            try {
                // Tenta obter ID via reflexão
                java.lang.reflect.Method getId = item.getClass().getMethod("getId");
                int id = (int) getId.invoke(item);
                selecionadosIds.add(id);
                System.out.println("Adicionado: " + nomeMapper.apply(item));
            } catch (Exception e) {
                System.out.println("Erro ao obter ID do item.");
            }
        }
        return selecionadosIds;
    }

    private void selecionarContextoRestaurante() {
        List<String> rests = controller.listarRestaurantes();
        System.out.println("Escolha o ID do Restaurante para Gerir:");
        rests.forEach(System.out::println);
        int id = lerInt("ID: ");
        controller.selecionarRestauranteContexto(id);
    }

    private String lerString(String msg) { System.out.print(msg); return scanner.nextLine(); }
    private int lerInt(String msg) { 
        while(true) { try { System.out.print(msg); return Integer.parseInt(scanner.nextLine()); } catch(Exception e) { System.out.println("Número inválido."); } }
    }
    private double lerDouble(String msg) { 
        while(true) { try { System.out.print(msg); return Double.parseDouble(scanner.nextLine()); } catch(Exception e) { System.out.println("Número inválido."); } }
    }
    private void esperarEnter() { System.out.println("(Enter para continuar...)"); scanner.nextLine(); }
}
package dss2526.service.base;

import java.util.List;

import dss2526.domain.entity.*;

public interface IBaseFacade {

    // --- Restaurante Logic ---
    Restaurante registarRestaurante(Restaurante r);
    Restaurante obterRestaurante(Integer id);
    Boolean removerRestaurante(Integer id);
    List<Restaurante> listarRestaurantes();
    List<Restaurante> listarRestaurantesComCatalogo(Integer id);

    // --- Funcionario Logic ---
    Funcionario registarFuncionario(Funcionario f);
    Funcionario obterFuncionario(Integer id);
    Boolean removerFuncionario(Integer id);
    List<Funcionario> listarFuncionarios();
    List<Funcionario> listarFuncionariosDeRestaurante(Integer id);

    // --- Estacao Logic ---
    Estacao registarEstacao(Estacao e);
    Estacao obterEstacao(Integer id);
    Boolean removerEstacao(Integer id);
    List<Estacao> getEstacoes();
    List<Estacao> listarEstacoesDeRestaurante(Integer id);

    // --- Catalogo Logic ---
    Catalogo registarCatalogo(Catalogo c);
    Catalogo obterCatalogo(Integer id);
    Boolean removerCatalogo(Integer id);
    List<Catalogo> listarCatalogos();

    // --- Menu Logic ---
    Menu registarMenu(Menu m);
    Menu obterMenu(Integer id);
    Boolean removerMenu(Integer id);
    List<Menu> listarMenus();

    // --- Produto Logic ---
    Produto registarProduto(Produto p);
    Produto obterProduto(Integer id);
    Boolean removerProduto(Integer id);
    List<Produto> listarProdutos();

    // --- Ingrediente Logic ---
    Ingrediente registarIngrediente(Ingrediente i);
    Ingrediente obterIngrediente(Integer id);
    Boolean removerIngrediente(Integer id);
    List<Ingrediente> listarIngredientes();

    // --- Passo Logic ---
    Passo registarPasso(Passo p);
    Passo obterPasso(Integer id);
    Boolean removerPasso(Integer id);
    List<Passo> listarPassos();

    // --- Mensagem Logic ---
    Mensagem registarMensagem(Mensagem m);
    Mensagem obterMensagem(Integer id);
    Boolean removerMensagem(Integer id);
    List<Mensagem> listarMensagens();
    List<Mensagem> listarMensagensDeRestaurante(Integer id);

    // --- Tarefa Logic ---
    Tarefa registarTarefa(Tarefa t);
    Tarefa obterTarefa(Integer id);
    Boolean removerTarefa(Integer id);
    List<Tarefa> listarTarefas();
    List<Tarefa> listarTarefasDePedido(Integer id);
    List<Tarefa> listarTarefasDeProduto(Integer id);

    // --- Pedido Logic ---
    Pedido registarPedido(Pedido p);
    Pedido obterPedido(Integer id);
    Boolean removerPedido(Integer id);
    List<Pedido> listarPedidos();
    List<Pedido> listarPedidosDeRestaurante(Integer id);
}

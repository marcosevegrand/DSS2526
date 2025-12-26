package dss2526.service.base;

import java.util.List;

import dss2526.domain.entity.*;

public interface IBaseFacade {

    // --- Restaurante Logic ---
    Restaurante registarRestaurante(Restaurante r);
    Restaurante obterRestaurante(Integer id);
    Restaurante atualizarRestaurante(Restaurante r);
    Boolean removerRestaurante(Integer id);
    List<Restaurante> listarRestaurantes();
    List<Restaurante> listarRestaurantesComCatalogo(Integer id);

    // --- Funcionario Logic ---
    Funcionario registarFuncionario(Funcionario f);
    Funcionario obterFuncionario(Integer id);
    Funcionario atualizarFuncionario(Funcionario f);
    Boolean removerFuncionario(Integer id);
    List<Funcionario> listarFuncionarios();
    List<Funcionario> listarFuncionariosDeRestaurante(Integer id);

    // --- Estacao Logic ---
    Estacao registarEstacao(Estacao e);
    Estacao obterEstacao(Integer id);
    Estacao atualizarEstacao(Estacao e);
    Boolean removerEstacao(Integer id);
    List<Estacao> getEstacoes();
    List<Estacao> listarEstacoesDeRestaurante(Integer id);

    // --- Catalogo Logic ---
    Catalogo registarCatalogo(Catalogo c);
    Catalogo obterCatalogo(Integer id);
    Catalogo atualizarCatalogo(Catalogo c);
    Boolean removerCatalogo(Integer id);
    List<Catalogo> listarCatalogos();

    // --- Menu Logic ---
    Menu registarMenu(Menu m);
    Menu obterMenu(Integer id);
    Menu atualizarMenu(Menu m);
    Boolean removerMenu(Integer id);
    List<Menu> listarMenus();

    // --- Produto Logic ---
    Produto registarProduto(Produto p);
    Produto obterProduto(Integer id);
    Produto atualizarProduto(Produto p);
    Boolean removerProduto(Integer id);
    List<Produto> listarProdutos();

    // --- Ingrediente Logic ---
    Ingrediente registarIngrediente(Ingrediente i);
    Ingrediente obterIngrediente(Integer id);
    Ingrediente atualizarIngrediente(Ingrediente i);
    Boolean removerIngrediente(Integer id);
    List<Ingrediente> listarIngredientes();

    // --- Passo Logic ---
    Passo registarPasso(Passo p);
    Passo obterPasso(Integer id);
    Passo atualizarPasso(Passo p);
    Boolean removerPasso(Integer id);
    List<Passo> listarPassos();

    // --- Mensagem Logic ---
    Mensagem registarMensagem(Mensagem m);
    Mensagem obterMensagem(Integer id);
    Mensagem atualizarMensagem(Mensagem m);
    Boolean removerMensagem(Integer id);
    List<Mensagem> listarMensagens();
    List<Mensagem> listarMensagensDeRestaurante(Integer id);

    // --- Tarefa Logic ---
    Tarefa registarTarefa(Tarefa t);
    Tarefa obterTarefa(Integer id);
    Tarefa atualizarTarefa(Tarefa t);
    Boolean removerTarefa(Integer id);
    List<Tarefa> listarTarefas();
    List<Tarefa> listarTarefasDePedido(Integer id);
    List<Tarefa> listarTarefasDeProduto(Integer id);

    // --- Pedido Logic ---
    Pedido registarPedido(Pedido p);
    Pedido obterPedido(Integer id);
    Pedido atualizarPedido(Pedido p);
    Boolean removerPedido(Integer id);
    List<Pedido> listarPedidos();
    List<Pedido> listarPedidosDeRestaurante(Integer id);
}

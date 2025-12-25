package dss2526.service.base;

import java.util.List;

import dss2526.data.contract.*;
import dss2526.data.impl.*;
import dss2526.domain.entity.*;

public class BaseFacade implements IBaseFacade {

    RestauranteDAO restauranteDAO = RestauranteDAOImpl.getInstance();
    EstacaoDAO estacaoDAO = EstacaoDAOImpl.getInstance();

    // --- Restaurante Logic ---
    @Override
    public void registarRestaurante(Restaurante r) {
        restauranteDAO.create(r);
    }
    @Override
    public Restaurante obterRestaurante(Integer id) {
        return restauranteDAO.findById(id);
    }
    public Boolean removerRestaurante(Integer id) {
        return restauranteDAO.delete(id);
    }
    List<Restaurante> listarRestaurantes() {
        return restauranteDAO.findAll();
    }
    List<Restaurante> listarRestaurantesComCatalogo(Integer id) {
        return restauranteDAO.findByCatalogoId(id);
    }

    // --- Funcionario Logic ---
    void registarFuncionario(Funcionario f);
    Funcionario obterFuncionario(Integer id);
    Boolean removerFuncionario(Integer id);
    List<Funcionario> listarFuncionarios();
    List<Funcionario> listarFuncionariosDeRestaurante(Integer id);

    // --- Estacao Logic ---
    void registarEstacao(Estacao e);
    Funcionario obterEstacao(Integer id);
    Boolean removerEstacao(Integer id);
    List<Estacao> getEstacoes();
    List<Estacao> listarEstacoesDeRestaurante(Integer id);

    // --- Catalogo Logic ---
    void registarCatalogo(Catalogo c);
    Catalogo obterCatalogo(Integer id);
    Boolean removerCatalogo(Integer id);
    List<Catalogo> listarCatalogos();

    // --- Menu Logic ---
    void registarMenu(Menu m);
    Menu obterMenu(Integer id);
    Boolean removerMenu(Integer id);
    List<Menu> listarMenus();

    // --- Produto Logic ---
    void registarProduto(Produto p);
    Produto obterProduto(Integer id);
    Boolean removerProduto(Integer id);
    List<Produto> listarProdutos();

    // --- Ingrediente Logic ---
    void registarIngrediente(Ingrediente i);
    Ingrediente obterIngrediente(Integer id);
    Boolean removerIngrediente(Integer id);
    List<Ingrediente> listarIngredientes();

    // --- Passo Logic ---
    void registarPasso(Passo p);
    Passo obterPasso(Integer id);
    Boolean removerPasso(Integer id);
    List<Passo> listarPassos();
    List<Passo> listarPassosDeProduto(Integer id);

    // --- Mensagem Logic ---
    void registarMensagem(Mensagem m);
    Mensagem obterMensagem(Integer id);
    Boolean removerMensagem(Integer id);
    List<Mensagem> listarMensagens();
    List<Mensagem> listarMensagensDeRestaurante(Integer id);

    // --- Tarefa Logic ---
    void registarTarefa(Tarefa t);
    Tarefa obterTarefa(Integer id);
    Boolean removerTarefa(Integer id);
    List<Tarefa> listarTarefas();
    List<Tarefa> listarTarefasDeEstacao(Integer id);
    List<Tarefa> listarTarefasDePedido(Integer id);
    List<Tarefa> listarTarefasDeProduto(Integer id);
}

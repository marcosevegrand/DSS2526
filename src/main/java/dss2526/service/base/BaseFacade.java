package dss2526.service.base;

import java.util.List;

import dss2526.data.contract.*;
import dss2526.data.impl.*;
import dss2526.domain.entity.*;

public class BaseFacade implements IBaseFacade {

    protected final RestauranteDAO restauranteDAO = RestauranteDAOImpl.getInstance();
    protected final FuncionarioDAO funcionarioDAO = FuncionarioDAOImpl.getInstance();
    protected final EstacaoDAO estacaoDAO = EstacaoDAOImpl.getInstance();
    protected final CatalogoDAO catalogoDAO = CatalogoDAOImpl.getInstance();
    protected final MenuDAO menuDAO = MenuDAOImpl.getInstance();
    protected final ProdutoDAO produtoDAO = ProdutoDAOImpl.getInstance();
    protected final IngredienteDAO ingredienteDAO = IngredienteDAOImpl.getInstance();
    protected final PassoDAO passoDAO = PassoDAOImpl.getInstance();
    protected final TarefaDAO tarefaDAO = TarefaDAOImpl.getInstance();
    protected final MensagemDAO mensagemDAO = MensagemDAOImpl.getInstance();
    protected final PedidoDAO pedidoDAO = PedidoDAOImpl.getInstance();

    // --- Restaurante Logic ---
    @Override
    public void registarRestaurante(Restaurante r) {
        restauranteDAO.create(r);
    }
    @Override
    public Restaurante obterRestaurante(Integer id) {
        return restauranteDAO.findById(id);
    }
    @Override
    public Boolean removerRestaurante(Integer id) {
        return restauranteDAO.delete(id);
    }
    @Override
    public List<Restaurante> listarRestaurantes() {
        return restauranteDAO.findAll();
    }
    @Override
    public List<Restaurante> listarRestaurantesComCatalogo(Integer id) {
        return restauranteDAO.findAllByCatalogo(id);
    }

    // --- Funcionario Logic ---
    @Override
    public void registarFuncionario(Funcionario f) {
        funcionarioDAO.create(f);
    }
    @Override
    public Funcionario obterFuncionario(Integer id) {
        return funcionarioDAO.findById(id);
    }
    @Override
    public Boolean removerFuncionario(Integer id) {
        return funcionarioDAO.delete(id);
    }
    @Override
    public List<Funcionario> listarFuncionarios() {
        return funcionarioDAO.findAll();
    }
    @Override
    public List<Funcionario> listarFuncionariosDeRestaurante(Integer id) {
        return funcionarioDAO.findAllByRestaurante(id);
    }

    // --- Estacao Logic ---
    @Override
    public void registarEstacao(Estacao e) {
        estacaoDAO.create(e);
    }
    @Override
    public Estacao obterEstacao(Integer id) {
        return estacaoDAO.findById(id);
    }
    @Override public Boolean removerEstacao(Integer id) {
        return estacaoDAO.delete(id);
    }
    @Override
    public List<Estacao> getEstacoes() {
        return estacaoDAO.findAll();
    }
    @Override
    public List<Estacao> listarEstacoesDeRestaurante(Integer id) {
        return estacaoDAO.findAllByRestaurante(id);
    }

    // --- Catalogo Logic ---
    @Override
    public void registarCatalogo(Catalogo c) {
        catalogoDAO.create(c);
    }
    @Override
    public Catalogo obterCatalogo(Integer id) {
        return catalogoDAO.findById(id);
    }
    @Override
    public Boolean removerCatalogo(Integer id) {
        return catalogoDAO.delete(id);
    }
    @Override
    public List<Catalogo> listarCatalogos() {
        return catalogoDAO.findAll();
    }

    // --- Menu Logic ---
    @Override
    public void registarMenu(Menu m) {
        menuDAO.create(m);
    }
    @Override
    public Menu obterMenu(Integer id) {
        return menuDAO.findById(id);
    }
    @Override
    public Boolean removerMenu(Integer id) {
        return menuDAO.delete(id);
    }
    @Override
    public List<Menu> listarMenus() {
        return menuDAO.findAll();
    }

    // --- Produto Logic ---
    @Override
    public void registarProduto(Produto p) {
        produtoDAO.create(p);
    }
    @Override
    public Produto obterProduto(Integer id) {
        return produtoDAO.findById(id);
    }
    @Override
    public Boolean removerProduto(Integer id) {
        return produtoDAO.delete(id);
    }
    @Override
    public List<Produto> listarProdutos() {
        return produtoDAO.findAll();
    }

    // --- Ingrediente Logic ---
    @Override
    public void registarIngrediente(Ingrediente i) {
        ingredienteDAO.create(i);
    }
    @Override
    public Ingrediente obterIngrediente(Integer id) {
        return ingredienteDAO.findById(id);
    }
    @Override
    public Boolean removerIngrediente(Integer id) {
        return ingredienteDAO.delete(id);
    }
    @Override
    public List<Ingrediente> listarIngredientes() {
        return ingredienteDAO.findAll();
    }

    // --- Passo Logic ---
    @Override
    public void registarPasso(Passo p) {
        passoDAO.create(p);
    }
    @Override
    public Passo obterPasso(Integer id) {
        return passoDAO.findById(id);
    }
    @Override
    public Boolean removerPasso(Integer id) {
        return passoDAO.delete(id);
    }
    @Override
    public List<Passo> listarPassos() {
        return passoDAO.findAll();
    }

    // --- Mensagem Logic ---
    @Override
    public void registarMensagem(Mensagem m) {
        mensagemDAO.create(m);
    }
    @Override
    public Mensagem obterMensagem(Integer id) {
        return mensagemDAO.findById(id);
    }
    @Override
    public Boolean removerMensagem(Integer id) {
        return mensagemDAO.delete(id);
    }
    @Override
    public List<Mensagem> listarMensagens() {
        return mensagemDAO.findAll();
    }
    @Override
    public List<Mensagem> listarMensagensDeRestaurante(Integer id) {
        return mensagemDAO.findAllByRestaurante(id);
    }

    // --- Tarefa Logic ---
    @Override
    public void registarTarefa(Tarefa t) {
        tarefaDAO.create(t);
    }
    @Override
    public Tarefa obterTarefa(Integer id) {
        return tarefaDAO.findById(id);
    }
    @Override
    public Boolean removerTarefa(Integer id) {
        return tarefaDAO.delete(id);
    }
    @Override
    public List<Tarefa> listarTarefas() {
        return tarefaDAO.findAll();
    }
    @Override
    public List<Tarefa> listarTarefasDePedido(Integer id) {
        return tarefaDAO.findAllByPedido(id);
    }
    @Override
    public List<Tarefa> listarTarefasDeProduto(Integer id) {
        return tarefaDAO.findAllByProduto(id);
    }
}

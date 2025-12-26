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
    public Restaurante registarRestaurante(Restaurante r) {
        return restauranteDAO.create(r);
    }
    @Override
    public Restaurante obterRestaurante(Integer id) {
        return restauranteDAO.findById(id);
    }
    @Override
    public Restaurante atualizarRestaurante(Restaurante r) {
        return restauranteDAO.update(r);
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
    public Funcionario registarFuncionario(Funcionario f) {
        return funcionarioDAO.create(f);
    }
    @Override
    public Funcionario obterFuncionario(Integer id) {
        return funcionarioDAO.findById(id);
    }
    @Override
    public Funcionario atualizarFuncionario(Funcionario f) {
        return funcionarioDAO.update(f);
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
    public Estacao registarEstacao(Estacao e) {
        return estacaoDAO.create(e);
    }
    @Override
    public Estacao obterEstacao(Integer id) {
        return estacaoDAO.findById(id);
    }
    @Override
    public Estacao atualizarEstacao(Estacao e) {
        return estacaoDAO.update(e);
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
    public Catalogo registarCatalogo(Catalogo c) {
        return catalogoDAO.create(c);
    }
    @Override
    public Catalogo obterCatalogo(Integer id) {
        return catalogoDAO.findById(id);
    }
    @Override
    public Catalogo atualizarCatalogo(Catalogo c) {
        return catalogoDAO.update(c);
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
    public Menu registarMenu(Menu m) {
        return menuDAO.create(m);
    }
    @Override
    public Menu obterMenu(Integer id) {
        return menuDAO.findById(id);
    }
    @Override
    public Menu atualizarMenu(Menu m) {
        return menuDAO.update(m);
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
    public Produto registarProduto(Produto p) {
        return produtoDAO.create(p);
    }
    @Override
    public Produto obterProduto(Integer id) {
        return produtoDAO.findById(id);
    }
    @Override
    public Produto atualizarProduto(Produto p) {
        return produtoDAO.update(p);
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
    public Ingrediente registarIngrediente(Ingrediente i) {
        return ingredienteDAO.create(i);
    }
    @Override
    public Ingrediente obterIngrediente(Integer id) {
        return ingredienteDAO.findById(id);
    }
    @Override
    public Ingrediente atualizarIngrediente(Ingrediente i) {
        return ingredienteDAO.update(i);
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
    public Passo registarPasso(Passo p) {
        return passoDAO.create(p);
    }
    @Override
    public Passo obterPasso(Integer id) {
        return passoDAO.findById(id);
    }
    @Override
    public Passo atualizarPasso(Passo p) {
        return passoDAO.update(p);
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
    public Mensagem registarMensagem(Mensagem m) {
        return mensagemDAO.create(m);
    }
    @Override
    public Mensagem obterMensagem(Integer id) {
        return mensagemDAO.findById(id);
    }
    @Override
    public Mensagem atualizarMensagem(Mensagem m) {
        return mensagemDAO.update(m);
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
    public Tarefa registarTarefa(Tarefa t) {
        return tarefaDAO.create(t);
    }
    @Override
    public Tarefa obterTarefa(Integer id) {
        return tarefaDAO.findById(id);
    }
    @Override
    public Tarefa atualizarTarefa(Tarefa t) {
        return tarefaDAO.update(t);
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

    // --- Pedido Logic ---
    @Override
    public Pedido registarPedido(Pedido p) {
        return pedidoDAO.create(p);
    }
    @Override
    public Pedido obterPedido(Integer id) {
        return pedidoDAO.findById(id);
    }
    @Override
    public Pedido atualizarPedido(Pedido p) {
        return pedidoDAO.update(p);
    }
    @Override
    public Boolean removerPedido(Integer id) {
        return pedidoDAO.delete(id);
    }
    @Override
    public List<Pedido> listarPedidos() {
        return pedidoDAO.findAll();
    }
    @Override
    public List<Pedido> listarPedidosDeRestaurante(Integer id) {
        return pedidoDAO.findAllByRestaurante(id);
    }
}

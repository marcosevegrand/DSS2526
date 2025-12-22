package restaurante.business;

import restaurante.business.pedidos.Pedido;
import restaurante.business.pedidos.SubsistemaPedidos;
import restaurante.business.estatisticas.Estatisticas;
import restaurante.business.estatisticas.SubsistemaEstatisticas;
import restaurante.data.IRestauranteDAO;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the Facade pattern for business operations
 */
public class RestauranteFacade implements IRestauranteFacade {
    
    private SubsistemaPedidos subsistemaPedidos;
    private SubsistemaEstatisticas subsistemaEstatisticas;
    private IRestauranteDAO dao;
    
    public RestauranteFacade(IRestauranteDAO dao) {
        this.dao = dao;
        this.subsistemaPedidos = new SubsistemaPedidos(dao);
        this.subsistemaEstatisticas = new SubsistemaEstatisticas(dao);
    }
    
    @Override
    public boolean autenticarFuncionario(String codigo, String palavraPasse) {
        // TODO: Implement authentication logic
        return false;
    }
    
    @Override
    public boolean autenticarGerente(String codigo, String palavraPasse) {
        // TODO: Implement authentication logic
        return false;
    }
    
    @Override
    public void logout() {
        // TODO: Implement logout logic
    }
    
    @Override
    public Pedido iniciarPedido() {
        return subsistemaPedidos.criarNovoPedido();
    }
    
    @Override
    public void adicionarItemAoPedido(String pedidoId, String itemId, Map<String, Object> opcoes) {
        subsistemaPedidos.adicionarItem(pedidoId, itemId, opcoes);
    }
    
    @Override
    public void personalizarItem(String pedidoId, String itemId, List<String> ingredientesRemover, List<String> notas) {
        subsistemaPedidos.personalizarItem(pedidoId, itemId, ingredientesRemover, notas);
    }
    
    @Override
    public double calcularTotalPedido(String pedidoId) {
        return subsistemaPedidos.calcularTotal(pedidoId);
    }
    
    @Override
    public boolean processarPagamento(String pedidoId, String metodoPagamento) {
        return subsistemaPedidos.processarPagamento(pedidoId, metodoPagamento);
    }
    
    @Override
    public String finalizarPedido(String pedidoId) {
        return subsistemaPedidos.finalizarPedido(pedidoId);
    }
    
    @Override
    public List<Pedido> consultarFilaDePedidos(String postoTrabalho) {
        return subsistemaPedidos.obterFilaDePedidos(postoTrabalho);
    }
    
    @Override
    public void iniciarPreparacaoPedido(String pedidoId, String funcionarioId) {
        subsistemaPedidos.iniciarPreparacao(pedidoId, funcionarioId);
    }
    
    @Override
    public void concluirPreparacaoPedido(String pedidoId) {
        subsistemaPedidos.concluirPreparacao(pedidoId);
    }
    
    @Override
    public void reportarAtrasoNoStock(String pedidoId, String ingrediente) {
        subsistemaPedidos.reportarAtraso(pedidoId, ingrediente);
    }
    
    @Override
    public List<Pedido> consultarPedidosProntos() {
        return subsistemaPedidos.obterPedidosProntos();
    }
    
    @Override
    public void marcarPedidoComoEntregue(String pedidoId) {
        subsistemaPedidos.marcarComoEntregue(pedidoId);
    }
    
    @Override
    public void reportarProblemaComPedido(String pedidoId, String descricaoProblema) {
        subsistemaPedidos.reportarProblema(pedidoId, descricaoProblema);
    }
    
    @Override
    public Estatisticas consultarEstatisticasFaturacao(String dataInicio, String dataFim) {
        return subsistemaEstatisticas.gerarEstatisticasFaturacao(dataInicio, dataFim);
    }
    
    @Override
    public Estatisticas consultarEstatisticasAtendimento(String dataInicio, String dataFim) {
        return subsistemaEstatisticas.gerarEstatisticasAtendimento(dataInicio, dataFim);
    }
    
    @Override
    public Estatisticas consultarEstatisticasDesempenho(String dataInicio, String dataFim) {
        return subsistemaEstatisticas.gerarEstatisticasDesempenho(dataInicio, dataFim);
    }
}

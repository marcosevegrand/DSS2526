package dss2526.service.venda;

import dss2526.domain.entity.*;
import dss2526.service.base.IBaseFacade;
import java.util.List;

public interface IVendaFacade extends IBaseFacade {
    
    /** Cria uma nova instância de pedido associada ao restaurante. */
    Pedido iniciarPedido(Restaurante restaurante, Boolean paraLevar);
    
    /** Finaliza o estado do pedido para confirmado e retorna tempo de entrega estimado. */
    double finalizarPedido(Pedido pedido);

    /** Cancela um pedido em curso, removendo-o do sistema. */
    void cancelarPedido(Pedido pedido);
    
    /** Retorna produtos filtrados por stock e alergénios. */
    List<Produto> listarProdutosDisponiveis(Restaurante restaurante, List<String> alergenicos);
    
    /** RetornaparaLevar menus filtrados por stock e alergénios. */
    List<Menu> listarMenusDisponiveis(Restaurante restaurante, List<String> alergenicos);
    
    /** Adiciona uma linha ao pedido após validações de negócio. */
    Pedido adicionarLinhaAoPedido(Pedido pedido, LinhaPedido linha);
    
    /** Remove uma linha do pedido. */
    Pedido removerLinhaDoPedido(Pedido pedido, int index);
}
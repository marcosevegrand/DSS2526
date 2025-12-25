package dss2526.service.gestao;

import java.util.List;

import dss2526.domain.entity.LinhaProduto;
import dss2526.domain.entity.LinhaStock;
import dss2526.service.base.IBaseFacade;

public interface IGestaoFacade extends IBaseFacade {
    
    // --- Restaurant Administration ---
    void atribuirCatalogoARestaurante(Integer restauranteId, Integer catalogoId);
    void removerCatalogoDeRestaurante(Integer restauranteId);
    
    // --- Employee/Staff Management ---
    void atribuirFuncionarioARestaurante(Integer funcionarioId, Integer restauranteId);
    void removerFuncionarioDeRestaurante(Integer funcionarioId);
    
    // --- Station/Kitchen Setup ---
    void atribuirEstacaoARestaurante(Integer estacaoId, Integer restauranteId);
    void removerEstacaoDeRestaurante(Integer estacaoId);
    
    // --- Catalog/Menu Configuration ---
    void adicionarProdutoAoMenu(Integer menuId, Integer produtoId, Integer quantidade);
    void removerProdutoDoMenu(Integer menuId, Integer produtoId);
    void adicionarMenuAoCatalogo(Integer catalogoId, Integer menuId);
    void removerMenuDoCatalogo(Integer catalogoId, Integer menuId);
    
    // --- Ingredient/Stock Management ---
    void adicionarIngredienteAoStock(Integer restauranteId, Integer ingredienteId, double quantidade);
    void atualizarQuantidadeStock(Integer restauranteId, Integer ingredienteId, double novaQuantidade);
    void removerIngredienteDoStock(Integer restauranteId, Integer ingredienteId);
    LinhaStock obterLinhaStock(Integer restauranteId, Integer ingredienteId);
    List<LinhaStock> listarStockDeRestaurante(Integer restauranteId);
    
    // --- Product Recipe Management ---
    void adicionarIngredienteAoProduto(Integer produtoId, Integer ingredienteId, double quantidade);
    void removerIngredienteDoProduto(Integer produtoId, Integer ingredienteId);
    void adicionarPassoAoProduto(Integer produtoId, Integer passoId);
    void removerPassoDoProduto(Integer produtoId, Integer passoId);
    List<LinhaProduto> listarIngredientesDoProduto(Integer produtoId);
}
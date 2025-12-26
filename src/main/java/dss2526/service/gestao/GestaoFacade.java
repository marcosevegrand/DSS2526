package dss2526.service.gestao;

import dss2526.service.base.BaseFacade;

public class GestaoFacade extends BaseFacade implements IGestaoFacade {

    private static GestaoFacade instance;

    private GestaoFacade() {}

    public static synchronized GestaoFacade getInstance() {
        if (instance == null) {
            instance = new GestaoFacade();
        }
        return instance;
    }

    // // --- Restaurant Administration ---
    
    // @Override
    // public void atribuirCatalogoARestaurante(Integer restauranteId, Integer catalogoId) {
    //     Restaurante restaurante = restauranteDAO.findById(restauranteId);
    //     if (restaurante == null) {
    //         throw new IllegalArgumentException("Restaurante não encontrado: " + restauranteId);
    //     }
    //     Catalogo catalogo = catalogoDAO.findById(catalogoId);
    //     if (catalogo == null) {
    //         throw new IllegalArgumentException("Catálogo não encontrado: " + catalogoId);
    //     }
    //     restaurante.setCatalogoId(catalogoId);
    //     restauranteDAO.update(restaurante);
    // }

    // @Override
    // public void removerCatalogoDeRestaurante(Integer restauranteId) {
    //     Restaurante restaurante = restauranteDAO.findById(restauranteId);
    //     if (restaurante == null) {
    //         throw new IllegalArgumentException("Restaurante não encontrado: " + restauranteId);
    //     }
    //     restaurante.setCatalogoId(null);
    //     restauranteDAO.update(restaurante);
    // }

    // // --- Employee/Staff Management ---
    
    // @Override
    // public void atribuirFuncionarioARestaurante(Integer funcionarioId, Integer restauranteId) {
    //     Funcionario funcionario = funcionarioDAO.findById(funcionarioId);
    //     if (funcionario == null) {
    //         throw new IllegalArgumentException("Funcionário não encontrado: " + funcionarioId);
    //     }
    //     Restaurante restaurante = restauranteDAO.findById(restauranteId);
    //     if (restaurante == null) {
    //         throw new IllegalArgumentException("Restaurante não encontrado: " + restauranteId);
    //     }
        
    //     // Atualizar funcionário
    //     funcionario.setRestauranteId(restauranteId);
    //     funcionarioDAO.update(funcionario);
        
    //     // Atualizar lista no restaurante
    //     if (!restaurante.getFuncionarioIds().contains(funcionarioId)) {
    //         restaurante.addFuncionarioId(funcionarioId);
    //         restauranteDAO.update(restaurante);
    //     }
    // }

    // @Override
    // public void removerFuncionarioDeRestaurante(Integer funcionarioId) {
    //     Funcionario funcionario = funcionarioDAO.findById(funcionarioId);
    //     if (funcionario == null) {
    //         throw new IllegalArgumentException("Funcionário não encontrado: " + funcionarioId);
    //     }
        
    //     Integer restauranteId = funcionario.getRestauranteId();
    //     if (restauranteId != null) {
    //         Restaurante restaurante = restauranteDAO.findById(restauranteId);
    //         if (restaurante != null) {
    //             restaurante.removeFuncionarioId(funcionarioId);
    //             restauranteDAO.update(restaurante);
    //         }
    //     }
        
    //     funcionario.setRestauranteId(null);
    //     funcionarioDAO.update(funcionario);
    // }

    // // --- Station/Kitchen Setup ---
    
    // @Override
    // public void atribuirEstacaoARestaurante(Integer estacaoId, Integer restauranteId) {
    //     Estacao estacao = estacaoDAO.findById(estacaoId);
    //     if (estacao == null) {
    //         throw new IllegalArgumentException("Estação não encontrada: " + estacaoId);
    //     }
    //     Restaurante restaurante = restauranteDAO.findById(restauranteId);
    //     if (restaurante == null) {
    //         throw new IllegalArgumentException("Restaurante não encontrado: " + restauranteId);
    //     }
        
    //     // Atualizar estação
    //     estacao.setRestauranteId(restauranteId);
    //     estacaoDAO.update(estacao);
        
    //     // Atualizar lista no restaurante
    //     if (!restaurante.getEstacaoIds().contains(estacaoId)) {
    //         restaurante.addEstacaoId(estacaoId);
    //         restauranteDAO.update(restaurante);
    //     }
    // }

    // @Override
    // public void removerEstacaoDeRestaurante(Integer estacaoId) {
    //     Estacao estacao = estacaoDAO.findById(estacaoId);
    //     if (estacao == null) {
    //         throw new IllegalArgumentException("Estação não encontrada: " + estacaoId);
    //     }
        
    //     Integer restauranteId = estacao.getRestauranteId();
    //     if (restauranteId != 0) {
    //         Restaurante restaurante = restauranteDAO.findById(restauranteId);
    //         if (restaurante != null) {
    //             restaurante.removeEstacaoId(estacaoId);
    //             restauranteDAO.update(restaurante);
    //         }
    //     }
        
    //     estacao.setRestauranteId(0);
    //     estacaoDAO.update(estacao);
    // }

    // // --- Catalog/Menu Configuration ---
    
    // @Override
    // public void adicionarProdutoAoMenu(Integer menuId, Integer produtoId, Integer quantidade) {
    //     Menu menu = menuDAO.findById(menuId);
    //     if (menu == null) {
    //         throw new IllegalArgumentException("Menu não encontrado: " + menuId);
    //     }
    //     Produto produto = produtoDAO.findById(produtoId);
    //     if (produto == null) {
    //         throw new IllegalArgumentException("Produto não encontrado: " + produtoId);
    //     }
        
    //     LinhaMenu linhaMenu = new LinhaMenu();
    //     linhaMenu.setMenuId(menuId);
    //     linhaMenu.setProdutoId(produtoId);
    //     linhaMenu.setQuantidade(quantidade);
        
    //     menu.addLinha(linhaMenu);
    //     menuDAO.update(menu);
    // }

    // @Override
    // public void removerProdutoDoMenu(Integer menuId, Integer produtoId) {
    //     Menu menu = menuDAO.findById(menuId);
    //     if (menu == null) {
    //         throw new IllegalArgumentException("Menu não encontrado: " + menuId);
    //     }
        
    //     menu.getLinha().removeIf(linha -> linha.getProdutoId() == produtoId);
    //     menuDAO.update(menu);
    // }

    // @Override
    // public void adicionarMenuAoCatalogo(Integer catalogoId, Integer menuId) {
    //     Catalogo catalogo = catalogoDAO.findById(catalogoId);
    //     if (catalogo == null) {
    //         throw new IllegalArgumentException("Catálogo não encontrado: " + catalogoId);
    //     }
    //     Menu menu = menuDAO.findById(menuId);
    //     if (menu == null) {
    //         throw new IllegalArgumentException("Menu não encontrado: " + menuId);
    //     }
        
    //     if (!catalogo.getMenuIds().contains(menuId)) {
    //         catalogo.addMenuId(menuId);
    //         catalogoDAO.update(catalogo);
    //     }
    // }

    // @Override
    // public void removerMenuDoCatalogo(Integer catalogoId, Integer menuId) {
    //     Catalogo catalogo = catalogoDAO.findById(catalogoId);
    //     if (catalogo == null) {
    //         throw new IllegalArgumentException("Catálogo não encontrado: " + catalogoId);
    //     }
        
    //     catalogo.removeMenuId(menuId);
    //     catalogoDAO.update(catalogo);
    // }

    // // --- Ingredient/Stock Management ---
    
    // @Override
    // public void adicionarIngredienteAoStock(Integer restauranteId, Integer ingredienteId, double quantidade) {
    //     Restaurante restaurante = restauranteDAO.findById(restauranteId);
    //     if (restaurante == null) {
    //         throw new IllegalArgumentException("Restaurante não encontrado: " + restauranteId);
    //     }
    //     Ingrediente ingrediente = ingredienteDAO.findById(ingredienteId);
    //     if (ingrediente == null) {
    //         throw new IllegalArgumentException("Ingrediente não encontrado: " + ingredienteId);
    //     }
        
    //     // Verificar se já existe
    //     LinhaStock existente = restaurante.getStock().stream()
    //             .filter(linha -> linha.getIngredienteId() == ingredienteId)
    //             .findFirst()
    //             .orElse(null);
        
    //     if (existente != null) {
    //         existente.setQuantidade(existente.getQuantidade() + quantidade);
    //     } else {
    //         LinhaStock novaLinha = new LinhaStock(ingredienteId, quantidade);
    //         novaLinha.setRestauranteId(restauranteId);
    //         restaurante.addLinhaStock(novaLinha);
    //     }
        
    //     restauranteDAO.update(restaurante);
    // }

    // @Override
    // public void atualizarQuantidadeStock(Integer restauranteId, Integer ingredienteId, double novaQuantidade) {
    //     Restaurante restaurante = restauranteDAO.findById(restauranteId);
    //     if (restaurante == null) {
    //         throw new IllegalArgumentException("Restaurante não encontrado: " + restauranteId);
    //     }
        
    //     LinhaStock linha = restaurante.getStock().stream()
    //             .filter(l -> l.getIngredienteId() == ingredienteId)
    //             .findFirst()
    //             .orElseThrow(() -> new IllegalArgumentException(
    //                     "Ingrediente não encontrado no stock: " + ingredienteId));
        
    //     linha.setQuantidade(novaQuantidade);
    //     restauranteDAO.update(restaurante);
    // }

    // @Override
    // public void removerIngredienteDoStock(Integer restauranteId, Integer ingredienteId) {
    //     Restaurante restaurante = restauranteDAO.findById(restauranteId);
    //     if (restaurante == null) {
    //         throw new IllegalArgumentException("Restaurante não encontrado: " + restauranteId);
    //     }
        
    //     restaurante.getStock().removeIf(linha -> linha.getIngredienteId() == ingredienteId);
    //     restauranteDAO.update(restaurante);
    // }

    // @Override
    // public LinhaStock obterLinhaStock(Integer restauranteId, Integer ingredienteId) {
    //     Restaurante restaurante = restauranteDAO.findById(restauranteId);
    //     if (restaurante == null) {
    //         return null;
    //     }
        
    //     return restaurante.getStock().stream()
    //             .filter(linha -> linha.getIngredienteId() == ingredienteId)
    //             .findFirst()d
    //             .orElse(null);
    // }

    // @Override
    // public List<LinhaStock> listarStockDeRestaurante(Integer restauranteId) {
    //     Restaurante restaurante = restauranteDAO.findById(restauranteId);
    //     if (restaurante == null) {
    //         throw new IllegalArgumentException("Restaurante não encontrado: " + restauranteId);
    //     }
    //     return restaurante.getStock();
    // }

    // // --- Product Recipe Management ---
    
    // @Override
    // public void adicionarIngredienteAoProduto(Integer produtoId, Integer ingredienteId, double quantidade) {
    //     Produto produto = produtoDAO.findById(produtoId);
    //     if (produto == null) {
    //         throw new IllegalArgumentException("Produto não encontrado: " + produtoId);
    //     }
    //     Ingrediente ingrediente = ingredienteDAO.findById(ingredienteId);
    //     if (ingrediente == null) {
    //         throw new IllegalArgumentException("Ingrediente não encontrado: " + ingredienteId);
    //     }
        
    //     LinhaProduto linhaProduto = new LinhaProduto();
    //     linhaProduto.setProdutoId(produtoId);
    //     linhaProduto.setIngredienteId(ingredienteId);
    //     linhaProduto.setQuantidade(quantidade);
        
    //     produto.addLinha(linhaProduto);
    //     produtoDAO.update(produto);
    // }

    // @Override
    // public void removerIngredienteDoProduto(Integer produtoId, Integer ingredienteId) {
    //     Produto produto = produtoDAO.findById(produtoId);
    //     if (produto == null) {
    //         throw new IllegalArgumentException("Produto não encontrado: " + produtoId);
    //     }
        
    //     produto.getLinhas().removeIf(linha -> linha.getIngredienteId() == ingredienteId);
    //     produtoDAO.update(produto);
    // }

    // @Override
    // public void adicionarPassoAoProduto(Integer produtoId, Integer passoId) {
    //     Produto produto = produtoDAO.findById(produtoId);
    //     if (produto == null) {
    //         throw new IllegalArgumentException("Produto não encontrado: " + produtoId);
    //     }
    //     Passo passo = passoDAO.findById(passoId);
    //     if (passo == null) {
    //         throw new IllegalArgumentException("Passo não encontrado: " + passoId);
    //     }
        
    //     if (!produto.getPassoIds().contains(passoId)) {
    //         produto.addPassoId(passoId);
    //         produtoDAO.update(produto);
    //     }
    // }

    // @Override
    // public void removerPassoDoProduto(Integer produtoId, Integer passoId) {
    //     Produto produto = produtoDAO.findById(produtoId);
    //     if (produto == null) {
    //         throw new IllegalArgumentException("Produto não encontrado: " + produtoId);
    //     }
        
    //     produto.removePassoId(passoId);
    //     produtoDAO.update(produto);
    // }

    // @Override
    // public List<LinhaProduto> listarIngredientesDoProduto(Integer produtoId) {
    //     Produto produto = produtoDAO.findById(produtoId);
    //     if (produto == null) {
    //         throw new IllegalArgumentException("Produto não encontrado: " + produtoId);
    //     }
    //     return produto.getLinhas();
    // }
}
package dss2526.service.gestao;

import dss2526.domain.entity.*;
import java.util.List;

public interface IGestaoFacade {
    // Funcionario
    Funcionario registarFuncionario(Funcionario func);
    List<Funcionario> listarFuncionarios();

    // Restaurante
    Restaurante criarRestaurante(Restaurante rest);

    // Menu/Produto
    Menu criarMenu(Menu menu);
    Produto criarProduto(Produto produto);
    List<Menu> getMenus();

    // Ingrediente
    Ingrediente criarIngrediente(Ingrediente ing);
}
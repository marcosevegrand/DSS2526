package pt.uminho.dss.restaurante.core.domain.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import pt.uminho.dss.restaurante.core.domain.enumeration.TipoItem;

public class Catalogo {

    private final List<Produto> produtos;
    private final List<Menu> menus;

    public Catalogo() {
        this.produtos = new ArrayList<>();
        this.menus = new ArrayList<>();
    }

    // -----------------------------------------
    // Métodos de gestão (pelo gerente / backoffice)
    // -----------------------------------------

    public void adicionarProduto(Produto p) {
        if (p == null) {
            throw new IllegalArgumentException("Produto não pode ser null");
        }
        produtos.add(p);
    }

    public void removerProduto(int idProduto) {
        produtos.removeIf(p -> p.getId() == idProduto);
    }

    public void adicionarMenu(Menu m) {
        if (m == null) {
            throw new IllegalArgumentException("Menu não pode ser null");
        }
        menus.add(m);
    }

    public void removerMenu(int idMenu) {
        menus.removeIf(m -> m.getId() == idMenu);
    }

    // -----------------------------------------
    // Métodos de consulta para a Venda
    // -----------------------------------------

    public List<Produto> listarProdutos() {
        return Collections.unmodifiableList(produtos);
    }

    public List<Menu> listarMenus() {
        return Collections.unmodifiableList(menus);
    }

    public Optional<Produto> obterProdutoPorId(int idProduto) {
        return produtos
            .stream()
            .filter(p -> p.getId() == idProduto)
            .findFirst();
    }

    public Optional<Menu> obterMenuPorId(int idMenu) {
        return menus
            .stream()
            .filter(m -> m.getId() == idMenu)
            .findFirst();
    }

    /**
     * Se quiseres tratar Produto/Menu de forma genérica
     * usando um único id vindo da UI.
     */
    public Object obterItemCatalogo(int idItem, TipoItem tipo) {
        switch (tipo) {
            case PRODUTO:
                return obterProdutoPorId(idItem).orElse(null);
            case MENU:
                return obterMenuPorId(idItem).orElse(null);
            default:
                return null;
        }
    }
}

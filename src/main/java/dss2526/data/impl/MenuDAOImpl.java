package pt.uminho.dss.restaurante.persistence.impl;

import pt.uminho.dss.restaurante.domain.entity.Menu;
import pt.uminho.dss.restaurante.persistence.contract.MenuDAO;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;

public class MenuDAOImpl implements MenuDAO {
    // ...existing code...
    private static final Map<Integer, Menu> MENUS = new ConcurrentHashMap<>();
    private static int nextId = 1;

    static {
        Menu m1 = new Menu();
        m1.setId(1);
        m1.setNome("Menu Simples");
        m1.setPreco(9.50);
        MENUS.put(m1.getId(), m1);

        Menu m2 = new Menu();
        m2.setId(2);
        m2.setNome("Menu Família");
        m2.setPreco(19.90);
        MENUS.put(m2.getId(), m2);

        nextId = 3;
    }

    @Override
    public Optional<Menu> findById(int id) {
        return Optional.ofNullable(MENUS.get(id));
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(MENUS.values());
    }

    @Override
    public Menu save(Menu menu) {
        if (menu.getId() == null || menu.getId() <= 0) {
            menu.setId(nextId++);
        } else {
            nextId = Math.max(nextId, menu.getId() + 1);
        }
        MENUS.put(menu.getId(), menu);
        return menu;
    }

    @Override
    public Menu update(Menu menu) {
        if (menu.getId() == null || !MENUS.containsKey(menu.getId())) {
            throw new IllegalArgumentException("Menu não existe: " + menu.getId());
        }
        MENUS.put(menu.getId(), menu);
        return menu;
    }

    @Override
    public void delete(int id) {
        MENUS.remove(id);
    }
}
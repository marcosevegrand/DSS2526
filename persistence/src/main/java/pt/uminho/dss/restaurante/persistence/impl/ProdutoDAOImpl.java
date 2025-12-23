package pt.uminho.dss.restaurante.persistence.impl;

import pt.uminho.dss.restaurante.domain.entity.Produto;
import pt.uminho.dss.restaurante.persistence.contract.ProdutoDAO;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;

public class ProdutoDAOImpl implements ProdutoDAO {
    // ...existing code...
    private static final Map<Integer, Produto> PRODUTOS = new ConcurrentHashMap<>();
    private static int nextId = 1;

    static {
        // Dados de exemplo (usam setters para compatibilidade com diferentes construtores)
        Produto p1 = new Produto();
        p1.setId(1);
        p1.setNome("Hambúrguer Clássico");
        p1.setPreco(6.50f);
        PRODUTOS.put(p1.getId(), p1);

        Produto p2 = new Produto();
        p2.setId(2);
        p2.setNome("Cheeseburger");
        p2.setPreco(7.20f);
        PRODUTOS.put(p2.getId(), p2);

        Produto p3 = new Produto();
        p3.setId(3);
        p3.setNome("Batatas Fritas");
        p3.setPreco(3.50f);
        PRODUTOS.put(p3.getId(), p3);

        Produto p4 = new Produto();
        p4.setId(4);
        p4.setNome("Coca-Cola 33cl");
        p4.setPreco(1.80f);
        PRODUTOS.put(p4.getId(), p4);

        nextId = 5;
    }

    @Override
    public Optional<Produto> findById(int id) {
        return Optional.ofNullable(PRODUTOS.get(id));
    }

    @Override
    public List<Produto> findAll() {
        return new ArrayList<>(PRODUTOS.values());
    }

    @Override
    public Produto save(Produto entity) {
        if (entity.getId() <= 0) {
            entity.setId(nextId++);
        } else {
            nextId = Math.max(nextId, entity.getId() + 1);
        }
        PRODUTOS.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Produto update(Produto entity) {
        if (entity.getId() <= 0 || !PRODUTOS.containsKey(entity.getId())) {
            throw new IllegalArgumentException("Produto não existe: " + entity.getId());
        }
        PRODUTOS.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public void delete(int id) {
        PRODUTOS.remove(id);
    }
}
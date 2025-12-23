package pt.uminho.dss.restaurante.persistence.impl;

import pt.uminho.dss.restaurante.domain.entity.Ingrediente;
import pt.uminho.dss.restaurante.persistence.contract.IngredienteDAO;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;

/**
 * Implementação em memória do IngredienteDAO para testes/ UI.
 */
public class IngredienteDAOImpl implements IngredienteDAO {

    private static final Map<Integer, Ingrediente> INGREDIENTES = new ConcurrentHashMap<>();
    private static Integer nextId = 1L;

    static {
        // exemplos iniciais
        Ingrediente i1 = new Ingrediente();
        i1.setId(1L);
        i1.setNome("Carne");
        INGREDIENTES.put(i1.getId(), i1);

        Ingrediente i2 = new Ingrediente();
        i2.setId(2L);
        i2.setNome("Queijo");
        INGREDIENTES.put(i2.getId(), i2);

        Ingrediente i3 = new Ingrediente();
        i3.setId(3L);
        i3.setNome("Batata");
        INGREDIENTES.put(i3.getId(), i3);

        nextId = 4L;
    }

    @Override
    public Optional<Ingrediente> findById(int id) {
        return Optional.ofNullable(INGREDIENTES.get(id));
    }

    @Override
    public List<Ingrediente> findAll() {
        return new ArrayList<>(INGREDIENTES.values());
    }

    @Override
    public Ingrediente save(Ingrediente ingrediente) {
        Integer id = ingrediente.getId();
        if (id == null || id <= 0) {
            ingrediente.setId(nextId++);
        } else {
            nextId = Math.max(nextId, id + 1);
        }
        INGREDIENTES.put(ingrediente.getId(), ingrediente);
        return ingrediente;
    }

    @Override
    public Ingrediente update(Ingrediente ingrediente) {
        Integer id = ingrediente.getId();
        if (id == null || !INGREDIENTES.containsKey(id)) {
            throw new IllegalArgumentException("Ingrediente não existe: " + id);
        }
        INGREDIENTES.put(id, ingrediente);
        return ingrediente;
    }

    @Override
    public void delete(int id) {
        INGREDIENTES.remove(id);
    }
}
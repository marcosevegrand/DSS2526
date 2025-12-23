package pt.uminho.dss.restaurante.persistence.impl;

import pt.uminho.dss.restaurante.domain.entity.Pedido;
import pt.uminho.dss.restaurante.domain.enumeration.EstadoPedido;
import pt.uminho.dss.restaurante.persistence.contract.PedidoDAO;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.Optional;

public class PedidoDAOImpl implements PedidoDAO {
    // ...existing code...
    private static final Map<Integer, Pedido> PEDIDOS = new ConcurrentHashMap<>();
    private static int nextId = 1;

    @Override
    public Optional<Pedido> findById(int id) {
        return Optional.ofNullable(PEDIDOS.get(id));
    }

    @Override
    public List<Pedido> findAll() {
        return new ArrayList<>(PEDIDOS.values());
    }

    @Override
    public Pedido save(Pedido pedido) {
        if (pedido.getId() <= 0) {
            pedido.setId(nextId++);
        } else {
            nextId = Math.max(nextId, pedido.getId() + 1);
        }
        PEDIDOS.put(pedido.getId(), pedido);
        return pedido;
    }

    @Override
    public Pedido update(Pedido pedido) {
        if (pedido.getId() <= 0 || !PEDIDOS.containsKey(pedido.getId())) {
            throw new IllegalArgumentException("Pedido não existe: " + pedido.getId());
        }
        PEDIDOS.put(pedido.getId(), pedido);
        return pedido;
    }

    @Override
    public void delete(int id) {
        PEDIDOS.remove(id);
    }

    @Override
    public List<Pedido> findByEstado(EstadoPedido estado) {
        return PEDIDOS.values().stream()
            .filter(p -> p.getEstado() == estado)
            .collect(Collectors.toList());
    }

    @Override
    public List<Pedido> findByData(LocalDate data) {
        return PEDIDOS.values().stream()
            .filter(p -> p.getDataHoraCriacao() != null && p.getDataHoraCriacao().toLocalDate().equals(data))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Pedido> findUltimoPorTerminal(int idTerminal) {
        return PEDIDOS.values().stream()
            .filter(p -> p.getIdTerminal() == idTerminal)
            .max(Comparator.comparing(Pedido::getDataHoraCriacao));
    }
    // ...existing code...
}
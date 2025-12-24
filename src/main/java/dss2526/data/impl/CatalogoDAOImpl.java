package dss2526.data.impl;

import dss2526.domain.entity.Catalogo;
import dss2526.domain.contract.Item;
import dss2526.domain.entity.Produto;
import dss2526.domain.entity.Menu;
import dss2526.domain.entity.Ingrediente;
import dss2526.data.config.DBConfig;
import dss2526.data.contract.CatalogoDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Ainda não esta implemnetado totalmente
public class CatalogoDAOImpl implements CatalogoDAO {

    @Override
    public void put(Integer key, Catalogo value) {
        try (Connection conn = DBConfig.getConnection()) {
            conn.setAutoCommit(false); // Transaction
            try {
                // Upsert Catalogo
                if (containsKey(conn, key)) {
                    // Update if needed
                    // Assuming generic update
                } else {
                    try (PreparedStatement ps = conn.prepareStatement("INSERT INTO catalogos (id) VALUES (?)")) {
                        ps.setInt(1, key);
                        ps.executeUpdate();
                    }
                }

                // Replace relations
                clearRelations(conn, key);
                insertRelations(conn, key, value);

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao guardar catalogo", e);
        }
    }

    private void clearRelations(Connection conn, Integer catalogoId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM catalogo_produtos WHERE catalogo_id=?")) {
            ps.setInt(1, catalogoId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM catalogo_menus WHERE catalogo_id=?")) {
            ps.setInt(1, catalogoId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM catalogo_ingredientes WHERE catalogo_id=?")) {
            ps.setInt(1, catalogoId);
            ps.executeUpdate();
        }
        // Alergenicos?
    }

    private void insertRelations(Connection conn, Integer catalogoId, Catalogo value) throws SQLException {
        for (Item item : value.getItems()) {
            if (item instanceof Produto) {
                try (PreparedStatement ps = conn
                        .prepareStatement("INSERT INTO catalogo_produtos (catalogo_id, produto_id) VALUES (?, ?)")) {
                    ps.setInt(1, catalogoId);
                    ps.setInt(2, item.getId()); // Item now has getId()
                    ps.executeUpdate();
                }
            } else if (item instanceof Menu) {
                try (PreparedStatement ps = conn
                        .prepareStatement("INSERT INTO catalogo_menus (catalogo_id, menu_id) VALUES (?, ?)")) {
                    ps.setInt(1, catalogoId);
                    ps.setInt(2, item.getId());
                    ps.executeUpdate();
                }
            }
        }
        for (Ingrediente i : value.getIngredientes()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO catalogo_ingredientes (catalogo_id, ingrediente_id) VALUES (?, ?)")) {
                ps.setInt(1, catalogoId);
                ps.setInt(2, i.getId());
                ps.executeUpdate();
            }
        }
    }

    @Override
    public Catalogo get(Integer key) {
        Catalogo c = null;
        try (Connection conn = DBConfig.getConnection()) {
            if (containsKey(conn, key)) {
                c = new Catalogo();
                c.setId(key);
                loadDetails(conn, c);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar catalogo", e);
        }
        return c;
    }

    private void loadDetails(Connection conn, Catalogo c) throws SQLException {
        List<Item> items = new ArrayList<>();
        // Load Produtos
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT p.* FROM produtos p JOIN catalogo_produtos cp ON p.id = cp.produto_id WHERE cp.catalogo_id = ?")) {
            ps.setInt(1, c.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Produto p = new Produto();
                    p.setId(rs.getInt("id"));
                    p.setNome(rs.getString("nome"));
                    p.setPreco(rs.getBigDecimal("preco"));
                    p.setDisponivel(rs.getBoolean("disponivel"));
                    items.add(p);
                }
            }
        }
        // Load Menus
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT m.* FROM menus m JOIN catalogo_menus cm ON m.id = cm.menu_id WHERE cm.catalogo_id = ?")) {
            ps.setInt(1, c.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Menu m = new Menu();
                    m.setId(rs.getInt("id"));
                    m.setNome(rs.getString("nome"));
                    m.setPreco(rs.getBigDecimal("preco"));
                    m.setDisponivel(rs.getBoolean("disponivel"));
                    items.add(m);
                }
            }
        }
        c.setItems(items);

        // Load Ingredientes
        List<Ingrediente> ingredients = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT i.* FROM ingredientes i JOIN catalogo_ingredientes ci ON i.id = ci.ingrediente_id WHERE ci.catalogo_id = ?")) {
            ps.setInt(1, c.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ingrediente i = new Ingrediente();
                    i.setId(rs.getInt("id"));
                    i.setNome(rs.getString("nome"));
                    i.setUnidadeMedida(rs.getString("unidade_medida"));
                    // Alergenico enum handling
                    ingredients.add(i);
                }
            }
        }
        c.setIngredientes(ingredients);
    }

    @Override
    public Catalogo remove(Integer key) {
        // Not implemented fully
        return null;
    }

    @Override
    public boolean containsKey(Integer key) {
        try (Connection conn = DBConfig.getConnection()) {
            return containsKey(conn, key);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean containsKey(Connection conn, Integer key) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM catalogos WHERE id=?")) {
            ps.setInt(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public List<Catalogo> values() {
        return new ArrayList<>(); // Stub
    }

    @Override
    public int size() {
        return 0; // Stub
    }
}

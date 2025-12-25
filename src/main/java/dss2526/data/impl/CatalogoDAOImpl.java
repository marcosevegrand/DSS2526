package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.CatalogoDAO;
import dss2526.domain.entity.Catalogo;

import java.sql.*;
import java.util.*;

public class CatalogoDAOImpl implements CatalogoDAO {
    private static CatalogoDAOImpl instance;
    private DBConfig dbConfig;

    // Identity Map for Catalogo
    private Map<Integer, Catalogo> catalogoMap = new HashMap<>();

    private CatalogoDAOImpl() {
        this.dbConfig = DBConfig.getInstance();
    }

    public static synchronized CatalogoDAOImpl getInstance() {
        if (instance == null) {
            instance = new CatalogoDAOImpl();
        }
        return instance;
    }

    @Override
    public Catalogo create(Catalogo entity) {
        String sql = "INSERT INTO Catalogo (nome) VALUES (?)";
        String sqlMenu = "INSERT INTO Catalogo_Menu (catalogo_id, menu_id) VALUES (?, ?)";
        String sqlProd = "INSERT INTO Catalogo_Produto (catalogo_id, produto_id) VALUES (?, ?)";

        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, entity.getNome());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setId(rs.getInt(1));
                        catalogoMap.put(entity.getId(), entity);
                    }
                }

                insertRelations(conn, sqlMenu, entity.getId(), entity.getMenuIds());
                insertRelations(conn, sqlProd, entity.getId(), entity.getProdutoIds());

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    private void insertRelations(Connection conn, String sql, int catId, List<Integer> ids) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Integer id : ids) {
                ps.setInt(1, catId);
                ps.setInt(2, id);
                ps.executeUpdate();
            }
        }
    }

    @Override
    public Catalogo update(Catalogo entity) {
        String sql = "UPDATE Catalogo SET nome=? WHERE id=?";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, entity.getNome());
                ps.setInt(2, entity.getId());
                ps.executeUpdate();

                deleteRelations(conn, entity.getId());
                String sqlMenu = "INSERT INTO Catalogo_Menu (catalogo_id, menu_id) VALUES (?, ?)";
                String sqlProd = "INSERT INTO Catalogo_Produto (catalogo_id, produto_id) VALUES (?, ?)";
                insertRelations(conn, sqlMenu, entity.getId(), entity.getMenuIds());
                insertRelations(conn, sqlProd, entity.getId(), entity.getProdutoIds());

                conn.commit();
                // Update map reference just in case, though it should be the same object
                catalogoMap.put(entity.getId(), entity);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    private void deleteRelations(Connection conn, int catId) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM Catalogo_Menu WHERE catalogo_id=" + catId);
            stmt.executeUpdate("DELETE FROM Catalogo_Produto WHERE catalogo_id=" + catId);
        }
    }

    @Override
    public Catalogo findById(Integer id) {
        if (catalogoMap.containsKey(id)) {
            return catalogoMap.get(id);
        }

        String sql = "SELECT * FROM Catalogo WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Catalogo c = new Catalogo();
                    c.setId(rs.getInt("id"));
                    c.setNome(rs.getString("nome"));
                    c.setMenuIds(findIds(conn, "Catalogo_Menu", "catalogo_id", "menu_id", id));
                    c.setProdutoIds(findIds(conn, "Catalogo_Produto", "catalogo_id", "produto_id", id));
                    
                    catalogoMap.put(c.getId(), c);
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Integer> findIds(Connection conn, String table, String fkCol, String targetCol, int id) throws SQLException {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT " + targetCol + " FROM " + table + " WHERE " + fkCol + " = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(rs.getInt(targetCol));
            }
        }
        return list;
    }

    @Override
    public List<Catalogo> findAll() {
        List<Catalogo> list = new ArrayList<>();
        String sql = "SELECT id FROM Catalogo";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(findById(rs.getInt("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Catalogo WHERE id = ?";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                deleteRelations(conn, id);
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                conn.commit();
                catalogoMap.remove(id);
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
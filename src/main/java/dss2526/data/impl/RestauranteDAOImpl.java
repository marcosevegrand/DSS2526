package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.RestauranteDAO;
import dss2526.domain.entity.LinhaStock;
import dss2526.domain.entity.Restaurante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RestauranteDAOImpl implements RestauranteDAO {

    private static RestauranteDAOImpl instance;
    private final DBConfig dbConfig = DBConfig.getInstance();

    public static RestauranteDAOImpl getInstance() {
        if (instance == null) {
            instance = new RestauranteDAOImpl();
        }
        return instance;
    }

    private RestauranteDAOImpl() {}

    @Override
    public Restaurante create(Restaurante restaurante) {
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            String sql = "INSERT INTO Restaurante (Nome, Localizacao) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, restaurante.getNome());
                stmt.setString(2, restaurante.getLocalizacao());
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        restaurante.setId(rs.getInt(1));
                    }
                }
            }

            // Save Composition: Stock (LinhaStock)
            if (restaurante.getStock() != null && !restaurante.getStock().isEmpty()) {
                String sqlStock = "INSERT INTO LinhaStock (RestauranteId, IngredienteId, Quantidade) VALUES (?, ?, ?)";
                try (PreparedStatement stmtStock = conn.prepareStatement(sqlStock)) {
                    for (LinhaStock ls : restaurante.getStock()) {
                        stmtStock.setInt(1, restaurante.getId());
                        stmtStock.setInt(2, ls.getIdIngrediente());
                        stmtStock.setDouble(3, ls.getQuantidade());
                        stmtStock.addBatch();
                    }
                    stmtStock.executeBatch();
                }
            }

            conn.commit();
            return restaurante;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public Restaurante findById(Integer id) {
        Restaurante r = null;
        try (Connection conn = dbConfig.getConnection()) {
            // Read Restaurante
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Restaurante WHERE Id = ?")) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        r = new Restaurante();
                        r.setId(rs.getInt("Id"));
                        r.setNome(rs.getString("Nome"));
                        r.setLocalizacao(rs.getString("Localizacao"));
                    }
                }
            }

            if (r != null) {
                // Read Composition: Stock
                String sqlStock = "SELECT * FROM LinhaStock WHERE RestauranteId = ?";
                List<LinhaStock> stock = new ArrayList<>();
                try (PreparedStatement stmt = conn.prepareStatement(sqlStock)) {
                    stmt.setInt(1, r.getId());
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            LinhaStock ls = new LinhaStock();
                            ls.setId(rs.getInt("Id"));
                            ls.setIdIngrediente(rs.getInt("IngredienteId"));
                            ls.setQuantidade(rs.getDouble("Quantidade"));
                            stock.add(ls);
                        }
                    }
                }
                r.setStock(stock);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return r;
    }

    @Override
    public Restaurante update(Restaurante restaurante) {
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            // Update Restaurante
            String sql = "UPDATE Restaurante SET Nome = ?, Localizacao = ? WHERE Id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, restaurante.getNome());
                stmt.setString(2, restaurante.getLocalizacao());
                stmt.setInt(3, restaurante.getId());
                stmt.executeUpdate();
            }

            // Update Stock (Delete all + Insert new)
            try (PreparedStatement stmtDel = conn.prepareStatement("DELETE FROM LinhaStock WHERE RestauranteId = ?")) {
                stmtDel.setInt(1, restaurante.getId());
                stmtDel.executeUpdate();
            }

            if (restaurante.getStock() != null && !restaurante.getStock().isEmpty()) {
                String sqlStock = "INSERT INTO LinhaStock (RestauranteId, IngredienteId, Quantidade) VALUES (?, ?, ?)";
                try (PreparedStatement stmtStock = conn.prepareStatement(sqlStock)) {
                    for (LinhaStock ls : restaurante.getStock()) {
                        stmtStock.setInt(1, restaurante.getId());
                        stmtStock.setInt(2, ls.getIdIngrediente());
                        stmtStock.setDouble(3, ls.getQuantidade());
                        stmtStock.addBatch();
                    }
                    stmtStock.executeBatch();
                }
            }

            conn.commit();
            return restaurante;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public boolean delete(Integer id) {
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM LinhaStock WHERE RestauranteId = ?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

            int result;
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Restaurante WHERE Id = ?")) {
                stmt.setInt(1, id);
                result = stmt.executeUpdate();
            }

            conn.commit();
            return result > 0;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public List<Restaurante> findAll() {
        List<Restaurante> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT Id FROM Restaurante");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(findById(rs.getInt("Id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
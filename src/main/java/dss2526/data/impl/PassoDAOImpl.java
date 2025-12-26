package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.PassoDAO;
import dss2526.domain.entity.Passo;
import dss2526.domain.enumeration.Trabalho;

import java.sql.*;
import java.time.Duration;
import java.util.*;

public class PassoDAOImpl implements PassoDAO {
    private static PassoDAOImpl instance;
    private DBConfig dbConfig;

    // Identity Map for Passo
    private Map<Integer, Passo> passoMap = new HashMap<>();

    private PassoDAOImpl() {
        this.dbConfig = DBConfig.getInstance();
    }

    public static synchronized PassoDAOImpl getInstance() {
        if (instance == null) instance = new PassoDAOImpl();
        return instance;
    }

    @Override
    public Passo create(Passo entity) {
        String sql = "INSERT INTO Passo (nome, duracao_minutos, trabalho) VALUES (?, ?, ?)";
        String sqlIng = "INSERT INTO Passo_Ingrediente (passo_id, ingrediente_id) VALUES (?, ?)";

        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, entity.getNome());
                ps.setLong(2, entity.getDuracao().toMinutes());
                ps.setString(3, entity.getTrabalho().name());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setId(rs.getInt(1));
                        passoMap.put(entity.getId(), entity);
                    }
                }

                try (PreparedStatement psIng = conn.prepareStatement(sqlIng)) {
                    for (Integer iId : entity.getIngredienteIds()) {
                        psIng.setInt(1, entity.getId());
                        psIng.setInt(2, iId);
                        psIng.executeUpdate();
                    }
                }
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

    @Override
    public Passo update(Passo entity) {
        String sql = "UPDATE Passo SET nome=?, duracao_minutos=?, trabalho=? WHERE id=?";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, entity.getNome());
                ps.setLong(2, entity.getDuracao().toMinutes());
                ps.setString(3, entity.getTrabalho().name());
                ps.setInt(4, entity.getId());
                ps.executeUpdate();

                // Re-insert relations
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("DELETE FROM Passo_Ingrediente WHERE passo_id=" + entity.getId());
                }
                String sqlIng = "INSERT INTO Passo_Ingrediente (passo_id, ingrediente_id) VALUES (?, ?)";
                try (PreparedStatement psIng = conn.prepareStatement(sqlIng)) {
                    for (Integer iId : entity.getIngredienteIds()) {
                        psIng.setInt(1, entity.getId());
                        psIng.setInt(2, iId);
                        psIng.executeUpdate();
                    }
                }
                conn.commit();
                passoMap.put(entity.getId(), entity);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Passo findById(Integer id) {
        if (passoMap.containsKey(id)) {
            return passoMap.get(id);
        }

        String sql = "SELECT * FROM Passo WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Passo p = new Passo();
                    p.setId(rs.getInt("id"));
                    p.setNome(rs.getString("nome"));
                    p.setDuracao(Duration.ofMinutes(rs.getLong("duracao_minutos")));
                    p.setTrabalho(Trabalho.valueOf(rs.getString("trabalho")));
                    p.setIngredienteIds(findIngredients(conn, id));
                    
                    passoMap.put(p.getId(), p);
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Integer> findIngredients(Connection conn, int passoId) throws SQLException {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT ingrediente_id FROM Passo_Ingrediente WHERE passo_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, passoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(rs.getInt(1));
            }
        }
        return list;
    }

    @Override
    public List<Passo> findAll() {
        List<Passo> list = new ArrayList<>();
        String sql = "SELECT id FROM Passo ORDER BY id";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(findById(rs.getInt("id")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Passo WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) passoMap.remove(id);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.EstacaoDAO;
import dss2526.domain.entity.Estacao;
import dss2526.domain.enumeration.Trabalho;

import java.sql.*;
import java.util.*;

public class EstacaoDAOImpl implements EstacaoDAO {
    private static EstacaoDAOImpl instance;
    private DBConfig dbConfig;

    // Identity Map for Estacao
    private Map<Integer, Estacao> estacaoMap = new HashMap<>();

    private EstacaoDAOImpl() {
        this.dbConfig = DBConfig.getInstance();
    }

    public static synchronized EstacaoDAOImpl getInstance() {
        if (instance == null) instance = new EstacaoDAOImpl();
        return instance;
    }

    @Override
    public Estacao create(Estacao entity) {
        String sql = "INSERT INTO Estacao (restaurante_id, trabalho) VALUES (?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entity.getRestauranteId());
            ps.setString(2, entity.getTrabalho().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getInt(1));
                    estacaoMap.put(entity.getId(), entity);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Estacao update(Estacao entity) {
        String sql = "UPDATE Estacao SET restaurante_id=?, trabalho=? WHERE id=?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entity.getRestauranteId());
            ps.setString(2, entity.getTrabalho().name());
            ps.setInt(3, entity.getId());
            ps.executeUpdate();
            estacaoMap.put(entity.getId(), entity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Estacao findById(Integer id) {
        if (estacaoMap.containsKey(id)) {
            return estacaoMap.get(id);
        }

        String sql = "SELECT * FROM Estacao WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Estacao e = map(rs);
                    estacaoMap.put(e.getId(), e);
                    return e;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Estacao map(ResultSet rs) throws SQLException {
        Estacao e = new Estacao();
        e.setId(rs.getInt("id"));
        e.setRestauranteId(rs.getInt("restaurante_id"));
        e.setTrabalho(Trabalho.valueOf(rs.getString("trabalho")));
        return e;
    }

    @Override
    public List<Estacao> findAll() {
        List<Estacao> list = new ArrayList<>();
        String sql = "SELECT * FROM Estacao ORDER BY id";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                if (estacaoMap.containsKey(id)) {
                    list.add(estacaoMap.get(id));
                } else {
                    Estacao e = map(rs);
                    estacaoMap.put(e.getId(), e);
                    list.add(e);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Estacao> findAllByRestaurante(int restauranteId) {
        List<Estacao> list = new ArrayList<>();
        String sql = "SELECT * FROM Estacao WHERE restaurante_id = ? ORDER BY id";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, restauranteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    if (estacaoMap.containsKey(id)) {
                        list.add(estacaoMap.get(id));
                    } else {
                        Estacao e = map(rs);
                        estacaoMap.put(e.getId(), e);
                        list.add(e);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Estacao WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) estacaoMap.remove(id);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
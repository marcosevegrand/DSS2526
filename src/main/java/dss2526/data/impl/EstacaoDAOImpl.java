package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.EstacaoDAO;
import dss2526.domain.entity.Estacao;
import dss2526.domain.enumeration.Trabalho;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EstacaoDAOImpl implements EstacaoDAO {
    
    private static EstacaoDAOImpl instance;
    private final DBConfig dbConfig = DBConfig.getInstance();

    public static EstacaoDAOImpl getInstance() {
        if (instance == null) instance = new EstacaoDAOImpl();
        return instance;
    }

    private EstacaoDAOImpl() {}

    @Override
    public Estacao create(Estacao obj) {
        String sql = "INSERT INTO Estacao (RestauranteId, Trabalho) VALUES (?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, obj.getRestauranteId());
            stmt.setString(2, obj.getTrabalho().name());
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) obj.setId(rs.getInt(1));
            }
            return obj;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Estacao findById(Integer id) {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Estacao WHERE Id=?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Estacao e = new Estacao();
                    e.setId(rs.getInt("Id"));
                    e.setRestauranteId(rs.getInt("RestauranteId"));
                    e.setTrabalho(Trabalho.valueOf(rs.getString("Trabalho")));
                    return e;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Estacao update(Estacao obj) {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE Estacao SET RestauranteId=?, Trabalho=? WHERE Id=?")) {
            stmt.setInt(1, obj.getRestauranteId());
            stmt.setString(2, obj.getTrabalho().name());
            stmt.setInt(3, obj.getId());
            stmt.executeUpdate();
            return obj;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Estacao WHERE Id=?")) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Estacao> findAll() {
        List<Estacao> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Estacao");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Estacao e = new Estacao();
                e.setId(rs.getInt("Id"));
                e.setRestauranteId(rs.getInt("RestauranteId"));
                e.setTrabalho(Trabalho.valueOf(rs.getString("Trabalho")));
                list.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Estacao> findByRestaurante(int restauranteId) {
        List<Estacao> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Estacao WHERE RestauranteId=?")) {
            stmt.setInt(1, restauranteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Estacao e = new Estacao();
                    e.setId(rs.getInt("Id"));
                    e.setRestauranteId(rs.getInt("RestauranteId"));
                    e.setTrabalho(Trabalho.valueOf(rs.getString("Trabalho")));
                    list.add(e);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
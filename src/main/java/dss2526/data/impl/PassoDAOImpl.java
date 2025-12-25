package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.PassoDAO;
import dss2526.domain.entity.Passo;
import dss2526.domain.enumeration.Trabalho;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class PassoDAOImpl implements PassoDAO {
    private static PassoDAOImpl instance;
    private final DBConfig dbConfig = DBConfig.getInstance();

    public static PassoDAOImpl getInstance() {
        if(instance == null) instance = new PassoDAOImpl();
        return instance;
    }

    private PassoDAOImpl() {}

    @Override
    public Passo create(Passo obj) {
        String sql = "INSERT INTO Passo (Nome, Duracao, Trabalho) VALUES (?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, obj.getNome());
            // Store duration as seconds (long)
            stmt.setLong(2, obj.getDuracao() != null ? obj.getDuracao().getSeconds() : 0);
            stmt.setString(3, obj.getTrabalho().name());
            
            stmt.executeUpdate();
            
            try(ResultSet rs = stmt.getGeneratedKeys()){
                if(rs.next()) obj.setId(rs.getInt(1));
            }
            return obj;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Passo findById(Integer id) {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Passo WHERE Id=?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return parsePasso(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Passo update(Passo obj) {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE Passo SET Nome=?, Duracao=?, Trabalho=? WHERE Id=?")) {
            stmt.setString(1, obj.getNome());
            stmt.setLong(2, obj.getDuracao() != null ? obj.getDuracao().getSeconds() : 0);
            stmt.setString(3, obj.getTrabalho().name());
            stmt.setInt(4, obj.getId());
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
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Passo WHERE Id=?")) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Passo> findAll() {
        List<Passo> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Passo");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(parsePasso(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Passo parsePasso(ResultSet rs) throws SQLException {
        Passo p = new Passo();
        p.setId(rs.getInt("Id"));
        p.setNome(rs.getString("Nome"));
        p.setDuracao(Duration.ofSeconds(rs.getLong("Duracao")));
        p.setTrabalho(Trabalho.valueOf(rs.getString("Trabalho")));
        return p;
    }
}
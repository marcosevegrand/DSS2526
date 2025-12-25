package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.MensagemDAO;
import dss2526.domain.entity.Mensagem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MensagemDAOImpl implements MensagemDAO {
    private DBConfig dbConfig = DBConfig.getInstance();
    private static Map<Integer, Mensagem> identityMap = new HashMap<>();

    @Override
    public Mensagem save(Mensagem m) {
        String sql = "INSERT INTO Mensagem (Texto, DataHora, Urgente) VALUES (?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, m.getTexto());
            ps.setTimestamp(2, Timestamp.valueOf(m.getDataHora()));
            ps.setBoolean(3, m.isUrgente());
            
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    m.setId(rs.getInt(1));
                }
            }
            identityMap.put(m.getId(), m);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return m;
    }

    @Override
    public Mensagem update(Mensagem m) {
        String sql = "UPDATE Mensagem SET Texto = ?, DataHora = ?, Urgente = ? WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, m.getTexto());
            ps.setTimestamp(2, Timestamp.valueOf(m.getDataHora()));
            ps.setBoolean(3, m.isUrgente());
            ps.setInt(4, m.getId());
            
            ps.executeUpdate();
            identityMap.put(m.getId(), m);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return m;
    }

    @Override
    public Mensagem findById(Integer id) {
        if (id == null || id <= 0) return null;
        if (identityMap.containsKey(id)) return identityMap.get(id);

        String sql = "SELECT * FROM Mensagem WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Mensagem m = new Mensagem();
                    m.setId(rs.getInt("ID"));
                    m.setTexto(rs.getString("Texto"));
                    m.setDataHora(rs.getTimestamp("DataHora").toLocalDateTime());
                    m.setUrgente(rs.getBoolean("Urgente"));
                    
                    identityMap.put(m.getId(), m);
                    return m;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Mensagem> findAll() {
        List<Mensagem> res = new ArrayList<>();
        String sql = "SELECT ID FROM Mensagem ORDER BY DataHora DESC";
        try (Connection conn = dbConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                res.add(findById(rs.getInt("ID")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null || id <= 0) return false;
        String sql = "DELETE FROM Mensagem WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            identityMap.remove(id);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
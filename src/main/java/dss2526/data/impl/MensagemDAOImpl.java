package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.MensagemDAO;
import dss2526.domain.entity.Mensagem;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MensagemDAOImpl implements MensagemDAO {

    private DBConfig dbConfig = DBConfig.getInstance();
    private static Map<Integer, Mensagem> identityMap = new HashMap<>();

    @Override
    public Mensagem save(Mensagem m) {
        String sql = (m.getId() > 0) ?
            "INSERT INTO Mensagens (ID, Texto, DataHora, Urgente) VALUES (?, ?, ?, ?)" :
            "INSERT INTO Mensagens (Texto, DataHora, Urgente) VALUES (?, ?, ?)";

        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            int idx = 1;
            if (m.getId() > 0) pstmt.setInt(idx++, m.getId());
            pstmt.setString(idx++, m.getTexto());
            pstmt.setTimestamp(idx++, Timestamp.valueOf(m.getDataHora()));
            pstmt.setBoolean(idx++, m.isUrgente());
            
            pstmt.executeUpdate();
            
            if (m.getId() == 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) m.setId(rs.getInt(1));
                }
            }
            identityMap.put(m.getId(), m);
            return m;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Mensagem findById(Integer id) {
        if (identityMap.containsKey(id)) return identityMap.get(id);

        String sql = "SELECT * FROM Mensagens WHERE ID = ?";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
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
        List<Mensagem> lista = new ArrayList<>();
        String sql = "SELECT ID FROM Mensagens";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) lista.add(findById(rs.getInt("ID")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Mensagem update(Mensagem m) {
        String sql = "UPDATE Mensagens SET Texto = ?, DataHora = ?, Urgente = ? WHERE ID = ?";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, m.getTexto());
            pstmt.setTimestamp(2, Timestamp.valueOf(m.getDataHora()));
            pstmt.setBoolean(3, m.isUrgente());
            pstmt.setInt(4, m.getId());
            pstmt.executeUpdate();
            identityMap.put(m.getId(), m);
            return m;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Mensagens WHERE ID = ?";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            if (pstmt.executeUpdate() > 0) {
                identityMap.remove(id);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
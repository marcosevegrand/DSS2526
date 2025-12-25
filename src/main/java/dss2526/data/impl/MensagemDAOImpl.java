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
    // Cache local para manter a identidade dos objetos carregados
    private static Map<Integer, Mensagem> identityMap = new HashMap<>();

    @Override
    public Mensagem save(Mensagem m) {
        String sql = "INSERT INTO Mensagem (Texto, DataHora, Urgente) VALUES (?, ?, ?)";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, m.getTexto());
                // Conversão de LocalDateTime para Timestamp SQL
                ps.setTimestamp(2, Timestamp.valueOf(m.getDataHora()));
                ps.setBoolean(3, m.isUrgente());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        m.setId(rs.getInt(1));
                    }
                }
                conn.commit();
                identityMap.put(m.getId(), m);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return m;
    }

    @Override
    public Mensagem findById(Integer id) {
        // Verifica se já está em memória
        if (identityMap.containsKey(id)) {
            return identityMap.get(id);
        }

        String sql = "SELECT * FROM Mensagem WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Mensagem m = new Mensagem();
                    m.setId(rs.getInt("ID"));
                    m.setTexto(rs.getString("Texto"));
                    
                    Timestamp ts = rs.getTimestamp("DataHora");
                    if (ts != null) {
                        m.setDataHora(ts.toLocalDateTime());
                    }
                    
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
        // Segue o padrão do ProdutoDAOImpl: busca IDs e usa findById para aproveitar o cache
        String sql = "SELECT ID FROM Mensagem";
        try (Connection conn = dbConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                res.add(findById(rs.getInt(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public Mensagem update(Mensagem m) {
        String sql = "UPDATE Mensagem SET Texto = ?, DataHora = ?, Urgente = ? WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, m.getTexto());
                ps.setTimestamp(2, Timestamp.valueOf(m.getDataHora()));
                ps.setBoolean(3, m.isUrgente());
                ps.setInt(4, m.getId());
                
                ps.executeUpdate();
                conn.commit();
                
                identityMap.put(m.getId(), m);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return m;
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Mensagem WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                identityMap.remove(id);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
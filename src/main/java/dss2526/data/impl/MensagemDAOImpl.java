package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.MensagemDAO;
import dss2526.domain.entity.Mensagem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MensagemDAOImpl implements MensagemDAO {
    private static MensagemDAOImpl instance;
    private final DBConfig dbConfig = DBConfig.getInstance();

    public static MensagemDAOImpl getInstance() {
        if(instance == null) instance = new MensagemDAOImpl();
        return instance;
    }

    private MensagemDAOImpl() {}

    @Override
    public Mensagem create(Mensagem obj) {
        String sql = "INSERT INTO Mensagem (Texto, DataHora, Urgente) VALUES (?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, obj.getTexto());
            stmt.setTimestamp(2, Timestamp.valueOf(obj.getDataHora()));
            stmt.setBoolean(3, obj.isUrgente());
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
    public Mensagem findById(Integer id) {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Mensagem WHERE Id=?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return parseMensagem(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Mensagem update(Mensagem obj) {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE Mensagem SET Texto=?, DataHora=?, Urgente=? WHERE Id=?")) {
            stmt.setString(1, obj.getTexto());
            stmt.setTimestamp(2, Timestamp.valueOf(obj.getDataHora()));
            stmt.setBoolean(3, obj.isUrgente());
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
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Mensagem WHERE Id=?")) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Mensagem> findAll() {
        List<Mensagem> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Mensagem");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(parseMensagem(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Mensagem parseMensagem(ResultSet rs) throws SQLException {
        Mensagem m = new Mensagem();
        m.setId(rs.getInt("Id"));
        m.setTexto(rs.getString("Texto"));
        m.setDataHora(rs.getTimestamp("DataHora").toLocalDateTime());
        m.setUrgente(rs.getBoolean("Urgente"));
        return m;
    }
}
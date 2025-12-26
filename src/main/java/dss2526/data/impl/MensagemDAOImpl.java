package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.MensagemDAO;
import dss2526.domain.entity.Mensagem;

import java.sql.*;
import java.util.*;

public class MensagemDAOImpl implements MensagemDAO {
    private static MensagemDAOImpl instance;
    private DBConfig dbConfig;

    // Identity Map for Mensagem
    private Map<Integer, Mensagem> mensagemMap = new HashMap<>();

    private MensagemDAOImpl() {
        this.dbConfig = DBConfig.getInstance();
    }

    public static synchronized MensagemDAOImpl getInstance() {
        if (instance == null) instance = new MensagemDAOImpl();
        return instance;
    }

    @Override
    public Mensagem create(Mensagem entity) {
        String sql = "INSERT INTO Mensagem (restaurante_id, texto, data_hora) VALUES (?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entity.getRestauranteId());
            ps.setString(2, entity.getTexto());
            ps.setTimestamp(3, Timestamp.valueOf(entity.getDataHora()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getInt(1));
                    mensagemMap.put(entity.getId(), entity);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Mensagem update(Mensagem entity) {
        String sql = "UPDATE Mensagem SET restaurante_id=?, texto=?, data_hora=? WHERE id=?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entity.getRestauranteId());
            ps.setString(2, entity.getTexto());
            ps.setTimestamp(3, Timestamp.valueOf(entity.getDataHora()));
            ps.setInt(4, entity.getId());
            ps.executeUpdate();
            mensagemMap.put(entity.getId(), entity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Mensagem findById(Integer id) {
        if (mensagemMap.containsKey(id)) {
            return mensagemMap.get(id);
        }
        
        String sql = "SELECT * FROM Mensagem WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Mensagem m = map(rs);
                    mensagemMap.put(m.getId(), m);
                    return m;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Mensagem map(ResultSet rs) throws SQLException {
        Mensagem m = new Mensagem();
        m.setId(rs.getInt("id"));
        m.setRestauranteId(rs.getInt("restaurante_id"));
        m.setTexto(rs.getString("texto"));
        m.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
        return m;
    }

    @Override
    public List<Mensagem> findAll() {
        List<Mensagem> list = new ArrayList<>();
        String sql = "SELECT * FROM Mensagem ORDER BY id";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                if (mensagemMap.containsKey(id)) {
                    list.add(mensagemMap.get(id));
                } else {
                    Mensagem m = map(rs);
                    mensagemMap.put(m.getId(), m);
                    list.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Mensagem> findAllByRestaurante(int restauranteId) {
        List<Mensagem> list = new ArrayList<>();
        String sql = "SELECT * FROM Mensagem WHERE restaurante_id = ? ORDER BY id";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, restauranteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    if (mensagemMap.containsKey(id)) {
                        list.add(mensagemMap.get(id));
                    } else {
                        Mensagem m = map(rs);
                        mensagemMap.put(m.getId(), m);
                        list.add(m);
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
        String sql = "DELETE FROM Mensagem WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) mensagemMap.remove(id);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
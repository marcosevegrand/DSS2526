package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.EstacaoDAO;
import dss2526.domain.entity.Estacao;
import dss2526.domain.enumeration.Trabalho;

import java.sql.*;
import java.util.*;

public class EstacaoDAOImpl implements EstacaoDAO {
    private static EstacaoDAOImpl instance;
    private final DBConfig dbConfig;
    private final Map<Integer, Estacao> estacaoMap = new HashMap<>();

    private EstacaoDAOImpl() { this.dbConfig = DBConfig.getInstance(); }

    public static synchronized EstacaoDAOImpl getInstance() {
        if (instance == null) instance = new EstacaoDAOImpl();
        return instance;
    }

    @Override
    public Estacao create(Estacao entity) {
        String sql = "INSERT INTO Estacao (restaurante_id, nome, tipo) VALUES (?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entity.getRestauranteId());
            ps.setString(2, entity.getNome());
            ps.setString(3, getDiscriminator(entity));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getInt(1));
                    saveTrabalhos(conn, entity);
                    estacaoMap.put(entity.getId(), entity);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return entity;
    }

    @Override
    public Estacao update(Estacao entity) {
        String sql = "UPDATE Estacao SET restaurante_id=?, nome=?, tipo=? WHERE id=?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entity.getRestauranteId());
            ps.setString(2, entity.getNome());
            ps.setString(3, getDiscriminator(entity));
            ps.setInt(4, entity.getId());
            ps.executeUpdate();
            saveTrabalhos(conn, entity);
            estacaoMap.put(entity.getId(), entity);
        } catch (SQLException e) { e.printStackTrace(); }
        return entity;
    }

    private void saveTrabalhos(Connection conn, Estacao e) throws SQLException {
        if (e instanceof Estacao.Cozinha cozinha) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Estacao_Trabalho WHERE estacao_id = ?")) {
                ps.setInt(1, e.getId());
                ps.executeUpdate();
            }
            String sql = "INSERT INTO Estacao_Trabalho (estacao_id, trabalho) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Trabalho t : cozinha.getEspecialidades()) {
                    ps.setInt(1, e.getId());
                    ps.setString(2, t.name());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    @Override
    public Estacao findById(Integer id) {
        if (estacaoMap.containsKey(id)) return estacaoMap.get(id);
        String sql = "SELECT * FROM Estacao WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Estacao e = map(rs, conn);
                    estacaoMap.put(e.getId(), e);
                    return e;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<Estacao> findAllByRestaurante(int rId) {
        List<Estacao> list = new ArrayList<>();
        String sql = "SELECT * FROM Estacao WHERE restaurante_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    if (estacaoMap.containsKey(id)) list.add(estacaoMap.get(id));
                    else {
                        Estacao e = map(rs, conn);
                        estacaoMap.put(e.getId(), e);
                        list.add(e);
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Estacao map(ResultSet rs, Connection conn) throws SQLException {
        String tipo = rs.getString("tipo");
        Estacao e = tipo.equals("CAIXA") ? new Estacao.Caixa() : new Estacao.Cozinha();
        e.setId(rs.getInt("id"));
        e.setRestauranteId(rs.getInt("restaurante_id"));
        e.setNome(rs.getString("nome"));
        if (e instanceof Estacao.Cozinha cozinha) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT trabalho FROM Estacao_Trabalho WHERE estacao_id = ?")) {
                ps.setInt(1, e.getId());
                try (ResultSet rsT = ps.executeQuery()) {
                    while (rsT.next()) cozinha.addEspecialidade(Trabalho.valueOf(rsT.getString("trabalho")));
                }
            }
        }
        return e;
    }

    private String getDiscriminator(Estacao e) { return (e instanceof Estacao.Caixa) ? "CAIXA" : "COZINHA"; }

    @Override
    public List<Estacao> findAll() {
        List<Estacao> list = new ArrayList<>();
        String sql = "SELECT * FROM Estacao ORDER BY id";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                if (estacaoMap.containsKey(id)) list.add(estacaoMap.get(id));
                else {
                    Estacao e = map(rs, conn);
                    estacaoMap.put(e.getId(), e);
                    list.add(e);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
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
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
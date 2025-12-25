package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.PassoDAO;
import dss2526.domain.entity.Passo;
import dss2526.domain.enumeration.Trabalho;

import java.sql.*;
import java.time.Duration;
import java.util.*;

public class PassoDAOImpl implements PassoDAO {

    private DBConfig dbConfig = DBConfig.getInstance();
    private static Map<Integer, Passo> identityMap = new HashMap<>();

    @Override
    public Passo save(Passo t) {
        String sql = "INSERT INTO Tarefa (Nome, DuracaoSegundos, Trabalho) VALUES (?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getNome());
            ps.setLong(2, t.getDuracao() != null ? t.getDuracao().getSeconds() : 0);
            ps.setString(3, t.getTrabalho() != null ? t.getTrabalho().name() : null);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    t.setId(rs.getInt(1));
                    identityMap.put(t.getId(), t);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return t;
    }

    @Override
    public Passo findById(Integer id) {
        if (identityMap.containsKey(id)) return identityMap.get(id);
        String sql = "SELECT * FROM Tarefa WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Passo t = new Passo();
                    t.setId(rs.getInt("ID"));
                    t.setNome(rs.getString("Nome"));
                    t.setDuracao(Duration.ofSeconds(rs.getLong("DuracaoSegundos")));
                    t.setTrabalho(Trabalho.valueOf(rs.getString("Trabalho")));
                    identityMap.put(t.getId(), t);
                    return t;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<Passo> findAll() {
        List<Passo> result = new ArrayList<>();
        String sql = "SELECT ID FROM Tarefa";
        try (Connection conn = dbConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) result.add(findById(rs.getInt(1)));
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    @Override
    public Passo update(Passo t) {
        String sql = "UPDATE Tarefa SET Nome = ?, DuracaoSegundos = ?, Trabalho = ? WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getNome());
            ps.setLong(2, t.getDuracao().getSeconds());
            ps.setString(3, t.getTrabalho().name());
            ps.setInt(4, t.getId());
            ps.executeUpdate();
            identityMap.put(t.getId(), t);
        } catch (SQLException e) { e.printStackTrace(); }
        return t;
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Tarefa WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                identityMap.remove(id);
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}
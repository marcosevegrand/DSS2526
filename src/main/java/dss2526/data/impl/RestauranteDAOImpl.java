package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.RestauranteDAO;
import dss2526.domain.entity.Restaurante;
import java.sql.*;
import java.util.*;

public class RestauranteDAOImpl implements RestauranteDAO {
    private DBConfig dbConfig = DBConfig.getInstance();
    private static Map<Integer, Restaurante> identityMap = new HashMap<>();

    @Override
    public Restaurante save(Restaurante r) {
        String sql = "INSERT INTO Restaurante (Nome, Localizacao) VALUES (?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getNome());
            ps.setString(2, r.getLocalizacao());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) r.setId(rs.getInt(1));
            }
            identityMap.put(r.getId(), r);
        } catch (SQLException e) { e.printStackTrace(); }
        return r;
    }

    @Override
    public Restaurante update(Restaurante r) {
        String sql = "UPDATE Restaurante SET Nome = ?, Localizacao = ? WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getNome());
            ps.setString(2, r.getLocalizacao());
            ps.setInt(3, r.getId());
            ps.executeUpdate();
            identityMap.put(r.getId(), r);
        } catch (SQLException e) { e.printStackTrace(); }
        return r;
    }

    @Override
    public Restaurante findById(Integer id) {
        if (id == null || id <= 0) return null;
        if (identityMap.containsKey(id)) return identityMap.get(id);
        
        String sql = "SELECT * FROM Restaurante WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Restaurante r = new Restaurante();
                    r.setId(rs.getInt("ID"));
                    r.setNome(rs.getString("Nome"));
                    r.setLocalizacao(rs.getString("Localizacao"));
                    identityMap.put(r.getId(), r);
                    return r;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<Restaurante> findAll() {
        List<Restaurante> res = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection(); 
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT ID FROM Restaurante")) {
            while (rs.next()) res.add(findById(rs.getInt(1)));
        } catch (SQLException e) { e.printStackTrace(); }
        return res;
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Restaurante WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            identityMap.remove(id);
            return rows > 0;
        } catch (SQLException e) { 
            System.err.println("Erro: Não é possível apagar restaurante com funcionários/pedidos ativos.");
            return false; 
        }
    }
}
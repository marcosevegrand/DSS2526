package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.IngredienteDAO;
import dss2526.domain.entity.Ingrediente;

import java.sql.*;
import java.util.*;

public class IngredienteDAOImpl implements IngredienteDAO {

    private DBConfig dbConfig = DBConfig.getInstance();
    private static Map<Integer, Ingrediente> identityMap = new HashMap<>();

    @Override
    public Ingrediente save(Ingrediente i) {
        String sql = "INSERT INTO Ingrediente (Nome, Unidade, Alergenico) VALUES (?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, i.getNome());
            ps.setString(2, i.getUnidade());
            ps.setString(3, i.getAlergenico());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    i.setId(rs.getInt(1));
                    identityMap.put(i.getId(), i);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return i;
    }

    @Override
    public Ingrediente findById(Integer id) {
        if (identityMap.containsKey(id)) return identityMap.get(id);
        String sql = "SELECT * FROM Ingrediente WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Ingrediente i = new Ingrediente(rs.getString("Nome"), rs.getString("Unidade"), rs.getString("Alergenico"));
                    i.setId(rs.getInt("ID"));
                    identityMap.put(i.getId(), i);
                    return i;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<Ingrediente> findAll() {
        List<Ingrediente> result = new ArrayList<>();
        String sql = "SELECT ID FROM Ingrediente";
        try (Connection conn = dbConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) result.add(findById(rs.getInt(1)));
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    @Override
    public Ingrediente update(Ingrediente i) {
        String sql = "UPDATE Ingrediente SET Nome = ?, Unidade = ?, Alergenico = ? WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, i.getNome());
            ps.setString(2, i.getUnidade());
            ps.setString(3, i.getAlergenico());
            ps.setInt(4, i.getId());
            ps.executeUpdate();
            identityMap.put(i.getId(), i);
        } catch (SQLException e) { e.printStackTrace(); }
        return i;
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Ingrediente WHERE ID = ?";
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
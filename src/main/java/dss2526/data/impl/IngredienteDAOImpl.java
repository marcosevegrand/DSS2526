package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.IngredienteDAO;
import dss2526.domain.entity.Ingrediente;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IngredienteDAOImpl implements IngredienteDAO {

    private DBConfig dbConfig = DBConfig.getInstance();
    private static Map<Integer, Ingrediente> identityMap = new HashMap<>();

    @Override
    public Ingrediente save(Ingrediente i) {
        String sql = (i.getId() > 0) ?
            "INSERT INTO Ingredientes (ID, Nome, Unidade, Alergenico) VALUES (?, ?, ?, ?)" :
            "INSERT INTO Ingredientes (Nome, Unidade, Alergenico) VALUES (?, ?, ?)";
            
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            int idx = 1;
            if (i.getId() > 0) pstmt.setInt(idx++, i.getId());
            pstmt.setString(idx++, i.getNome());
            pstmt.setString(idx++, i.getUnidade());
            pstmt.setString(idx++, i.getAlergenico());
            
            pstmt.executeUpdate();
            
            if (i.getId() == 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) i.setId(rs.getInt(1));
                }
            }
            identityMap.put(i.getId(), i);
            return i;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Ingrediente findById(Integer id) {
        if (identityMap.containsKey(id)) return identityMap.get(id);

        String sql = "SELECT * FROM Ingredientes WHERE ID = ?";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Ingrediente i = new Ingrediente();
                    i.setId(rs.getInt("ID"));
                    i.setNome(rs.getString("Nome"));
                    i.setUnidade(rs.getString("Unidade"));
                    i.setAlergenico(rs.getString("Alergenico"));
                    identityMap.put(i.getId(), i);
                    return i;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Ingrediente> findAll() {
        List<Ingrediente> lista = new ArrayList<>();
        String sql = "SELECT ID FROM Ingredientes";
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
    public Ingrediente update(Ingrediente i) {
        String sql = "UPDATE Ingredientes SET Nome = ?, Unidade = ?, Alergenico = ? WHERE ID = ?";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, i.getNome());
            pstmt.setString(2, i.getUnidade());
            pstmt.setString(3, i.getAlergenico());
            pstmt.setInt(4, i.getId());
            pstmt.executeUpdate();
            identityMap.put(i.getId(), i);
            return i;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Ingredientes WHERE ID = ?";
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
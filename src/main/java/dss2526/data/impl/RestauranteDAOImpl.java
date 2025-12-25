package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.RestauranteDAO;
import dss2526.domain.entity.Restaurante;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestauranteDAOImpl implements RestauranteDAO {

    private DBConfig dbConfig = DBConfig.getInstance();
    private static Map<Integer, Restaurante> identityMap = new HashMap<>();

    @Override
    public Restaurante save(Restaurante r) {
        String sql = (r.getId() > 0) ?
            "INSERT INTO Restaurante (ID, Nome, Localizacao) VALUES (?, ?, ?)" :
            "INSERT INTO Restaurante (Nome, Localizacao) VALUES (?, ?)";

        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            int idx = 1;
            if (r.getId() > 0) pstmt.setInt(idx++, r.getId());
            pstmt.setString(idx++, r.getNome());
            pstmt.setString(idx++, r.getLocalizacao());
            
            pstmt.executeUpdate();
            
            if (r.getId() == 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) r.setId(rs.getInt(1));
                }
            }
            identityMap.put(r.getId(), r);
            return r;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Restaurante findById(Integer id) {
        if (identityMap.containsKey(id)) return identityMap.get(id);

        String sql = "SELECT * FROM Restaurante WHERE ID = ?";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Restaurante r = new Restaurante();
                    r.setId(rs.getInt("ID"));
                    r.setNome(rs.getString("Nome"));
                    r.setLocalizacao(rs.getString("Localizacao"));
                    identityMap.put(r.getId(), r);
                    return r;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Restaurante> findAll() {
        List<Restaurante> lista = new ArrayList<>();
        String sql = "SELECT ID FROM Restaurante";
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
    public Restaurante update(Restaurante r) {
        String sql = "UPDATE Restaurante SET Nome = ?, Localizacao = ? WHERE ID = ?";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, r.getNome());
            pstmt.setString(2, r.getLocalizacao());
            pstmt.setInt(3, r.getId());
            pstmt.executeUpdate();
            identityMap.put(r.getId(), r);
            return r;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Restaurante WHERE ID = ?";
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
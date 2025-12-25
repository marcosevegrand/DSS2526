package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.MenuDAO;
import dss2526.domain.entity.Menu;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuDAOImpl implements MenuDAO {

    private DBConfig dbConfig = DBConfig.getInstance();
    private static Map<Integer, Menu> identityMap = new HashMap<>();

    @Override
    public Menu save(Menu m) {
        String sql = (m.getId() > 0) ?
            "INSERT INTO Menus (ID, Nome, Preco) VALUES (?, ?, ?)" :
            "INSERT INTO Menus (Nome, Preco) VALUES (?, ?)";

        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            int idx = 1;
            if (m.getId() > 0) pstmt.setInt(idx++, m.getId());
            pstmt.setString(idx++, m.getNome());
            pstmt.setDouble(idx++, m.getPreco());
            
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
    public Menu findById(Integer id) {
        if (identityMap.containsKey(id)) return identityMap.get(id);

        String sql = "SELECT * FROM Menus WHERE ID = ?";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Menu m = new Menu();
                    m.setId(rs.getInt("ID"));
                    m.setNome(rs.getString("Nome"));
                    m.setPreco(rs.getDouble("Preco"));
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
    public List<Menu> findAll() {
        List<Menu> lista = new ArrayList<>();
        String sql = "SELECT ID FROM Menus";
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
    public Menu update(Menu m) {
        String sql = "UPDATE Menus SET Nome = ?, Preco = ? WHERE ID = ?";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, m.getNome());
            pstmt.setDouble(2, m.getPreco());
            pstmt.setInt(3, m.getId());
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
        String sql = "DELETE FROM Menus WHERE ID = ?";
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
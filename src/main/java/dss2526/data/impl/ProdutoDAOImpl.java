package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.ProdutoDAO;
import dss2526.domain.entity.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProdutoDAOImpl implements ProdutoDAO {

    private DBConfig dbConfig = DBConfig.getInstance();
    private static Map<Integer, Produto> identityMap = new HashMap<>();

    @Override
    public Produto save(Produto p) {
        String sql = (p.getId() > 0) ?
            "INSERT INTO Produtos (ID, Nome, Preco) VALUES (?, ?, ?)" :
            "INSERT INTO Produtos (Nome, Preco) VALUES (?, ?)";

        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            int idx = 1;
            if (p.getId() > 0) pstmt.setInt(idx++, p.getId());
            pstmt.setString(idx++, p.getNome());
            pstmt.setDouble(idx++, p.getPreco());
            
            pstmt.executeUpdate();
            
            if (p.getId() == 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) p.setId(rs.getInt(1));
                }
            }
            identityMap.put(p.getId(), p);
            return p;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Produto findById(Integer id) {
        if (identityMap.containsKey(id)) return identityMap.get(id);

        String sql = "SELECT * FROM Produtos WHERE ID = ?";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Produto p = new Produto();
                    p.setId(rs.getInt("ID"));
                    p.setNome(rs.getString("Nome"));
                    p.setPreco(rs.getDouble("Preco"));
                    identityMap.put(p.getId(), p);
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Produto> findAll() {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT ID FROM Produtos";
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
    public Produto update(Produto p) {
        String sql = "UPDATE Produtos SET Nome = ?, Preco = ? WHERE ID = ?";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getNome());
            pstmt.setDouble(2, p.getPreco());
            pstmt.setInt(3, p.getId());
            pstmt.executeUpdate();
            identityMap.put(p.getId(), p);
            return p;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Produtos WHERE ID = ?";
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
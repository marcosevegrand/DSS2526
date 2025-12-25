package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.FuncionarioDAO;
import dss2526.domain.entity.Funcionario;
import dss2526.domain.enumeration.RoleTrabalhador;

import java.sql.*;
import java.util.*;

public class FuncionarioDAOImpl implements FuncionarioDAO {
    private DBConfig dbConfig = DBConfig.getInstance();
    private static Map<Integer, Funcionario> identityMap = new HashMap<>();

    @Override
    public Funcionario save(Funcionario f) {
        String sql = "INSERT INTO Funcionario (Nome, Username, Password, Papel, RestauranteID) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, f.getNome());
            ps.setString(2, f.getUsername());
            ps.setString(3, f.getPassword());
            ps.setString(4, f.getPapel().name());
            
            // Trata o RestauranteID como NULL se for 0 (caso do COO)
            if (f.getRestauranteId() == null || f.getRestauranteId() == 0) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, f.getRestauranteId());
            }

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) f.setId(rs.getInt(1));
            }
            identityMap.put(f.getId(), f);
        } catch (SQLException e) { e.printStackTrace(); }
        return f;
    }

    @Override
    public Funcionario update(Funcionario f) {
        String sql = "UPDATE Funcionario SET Nome = ?, Username = ?, Password = ?, Papel = ?, RestauranteID = ? WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, f.getNome());
            ps.setString(2, f.getUsername());
            ps.setString(3, f.getPassword());
            ps.setString(4, f.getPapel().name());
            
            if (f.getRestauranteId() == null || f.getRestauranteId() == 0) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, f.getRestauranteId());
            }
            
            ps.setInt(6, f.getId());
            ps.executeUpdate();
            identityMap.put(f.getId(), f);
        } catch (SQLException e) { e.printStackTrace(); }
        return f;
    }

    @Override
    public Funcionario findById(Integer id) {
        if (id == null || id <= 0) return null;
        if (identityMap.containsKey(id)) return identityMap.get(id);

        String sql = "SELECT * FROM Funcionario WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Funcionario f = new Funcionario();
                    f.setId(rs.getInt("ID"));
                    f.setNome(rs.getString("Nome"));
                    f.setUsername(rs.getString("Username"));
                    f.setPassword(rs.getString("Password"));
                    f.setPapel(RoleTrabalhador.valueOf(rs.getString("Papel")));
                    
                    int restId = rs.getInt("RestauranteID");
                    f.setRestauranteId(rs.wasNull() ? 0 : restId);
                    
                    identityMap.put(f.getId(), f);
                    return f;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<Funcionario> findByRestaurante(int restauranteId) {
        List<Funcionario> res = new ArrayList<>();
        String sql = "SELECT ID FROM Funcionario WHERE RestauranteID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, restauranteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    res.add(findById(rs.getInt("ID")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return res;
    }

    @Override
    public List<Funcionario> findAll() {
        List<Funcionario> res = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection(); 
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT ID FROM Funcionario")) {
            while (rs.next()) {
                res.add(findById(rs.getInt(1)));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return res;
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Funcionario WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            identityMap.remove(id);
            return rows > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
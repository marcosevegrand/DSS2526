package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.FuncionarioDAO;
import dss2526.domain.entity.Funcionario;
import dss2526.domain.enumeration.Funcao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FuncionarioDAOImpl implements FuncionarioDAO {

    private DBConfig dbConfig = DBConfig.getInstance();
    private static Map<Integer, Funcionario> identityMap = new HashMap<>();

    @Override
    public Funcionario save(Funcionario f) {
        String sql = (f.getId() > 0) ?
            "INSERT INTO Funcionarios (ID, Restaurante_ID, Utilizador, Password, Funcao) VALUES (?, ?, ?, ?, ?)" :
            "INSERT INTO Funcionarios (Restaurante_ID, Utilizador, Password, Funcao) VALUES (?, ?, ?, ?)";

        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            int idx = 1;
            if (f.getId() > 0) pstmt.setInt(idx++, f.getId());
            pstmt.setInt(idx++, f.getRestauranteId());
            pstmt.setString(idx++, f.getUtilizador());
            pstmt.setString(idx++, f.getPassword());
            pstmt.setString(idx++, f.getFuncao().name());
            
            pstmt.executeUpdate();

            if (f.getId() == 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) f.setId(rs.getInt(1));
                }
            }
            identityMap.put(f.getId(), f);
            return f;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Funcionario findById(Integer id) {
        if (identityMap.containsKey(id)) return identityMap.get(id);

        String sql = "SELECT * FROM Funcionarios WHERE ID = ?";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Funcionario f = new Funcionario();
                    f.setId(rs.getInt("ID"));
                    f.setRestauranteId(rs.getInt("Restaurante_ID"));
                    f.setUtilizador(rs.getString("Utilizador"));
                    f.setPassword(rs.getString("Password"));
                    f.setFuncao(Funcao.valueOf(rs.getString("Funcao")));
                    identityMap.put(f.getId(), f);
                    return f;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Funcionario> findAll() {
        List<Funcionario> lista = new ArrayList<>();
        String sql = "SELECT ID FROM Funcionarios";
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
    public Funcionario update(Funcionario f) {
        String sql = "UPDATE Funcionarios SET Restaurante_ID = ?, Utilizador = ?, Password = ?, Funcao = ? WHERE ID = ?";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, f.getRestauranteId());
            pstmt.setString(2, f.getUtilizador());
            pstmt.setString(3, f.getPassword());
            pstmt.setString(4, f.getFuncao().name());
            pstmt.setInt(5, f.getId());
            pstmt.executeUpdate();
            identityMap.put(f.getId(), f);
            return f;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Funcionarios WHERE ID = ?";
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

    @Override
    public Funcionario authenticate(int id, String password) {
        Funcionario f = findById(id);
        if (f != null && f.getPassword().equals(password)) {
            return f;
        }
        return null;
    }
}
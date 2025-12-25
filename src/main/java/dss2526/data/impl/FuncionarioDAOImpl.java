package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.FuncionarioDAO;
import dss2526.domain.entity.Funcionario;
import dss2526.domain.enumeration.Funcao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAOImpl implements FuncionarioDAO {

    private static FuncionarioDAOImpl instance;
    private final DBConfig dbConfig = DBConfig.getInstance();

    public static FuncionarioDAOImpl getInstance() {
        if (instance == null) {
            instance = new FuncionarioDAOImpl();
        }
        return instance;
    }

    private FuncionarioDAOImpl() {}

    @Override
    public Funcionario create(Funcionario funcionario) {
        String sql = "INSERT INTO Funcionario (RestauranteId, Utilizador, Password, Funcao) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            if (funcionario.getRestauranteId() != null) {
                stmt.setInt(1, funcionario.getRestauranteId());
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }
            stmt.setString(2, funcionario.getUtilizador());
            stmt.setString(3, funcionario.getPassword());
            stmt.setString(4, funcionario.getFuncao().name());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    funcionario.setId(rs.getInt(1));
                }
            }
            return funcionario;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Funcionario findById(Integer id) {
        String sql = "SELECT * FROM Funcionario WHERE Id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseFuncionario(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Funcionario update(Funcionario funcionario) {
        String sql = "UPDATE Funcionario SET RestauranteId = ?, Utilizador = ?, Password = ?, Funcao = ? WHERE Id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (funcionario.getRestauranteId() != null) {
                stmt.setInt(1, funcionario.getRestauranteId());
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }
            stmt.setString(2, funcionario.getUtilizador());
            stmt.setString(3, funcionario.getPassword());
            stmt.setString(4, funcionario.getFuncao().name());
            stmt.setInt(5, funcionario.getId());
            
            stmt.executeUpdate();
            return funcionario;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Funcionario WHERE Id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Funcionario> findAll() {
        List<Funcionario> lista = new ArrayList<>();
        String sql = "SELECT * FROM Funcionario";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                lista.add(parseFuncionario(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Funcionario> findByRestaurante(int restauranteId) {
        List<Funcionario> lista = new ArrayList<>();
        String sql = "SELECT * FROM Funcionario WHERE RestauranteId = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, restauranteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(parseFuncionario(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private Funcionario parseFuncionario(ResultSet rs) throws SQLException {
        Funcionario f = new Funcionario();
        f.setId(rs.getInt("Id"));
        f.setRestauranteId(rs.getObject("RestauranteId") != null ? rs.getInt("RestauranteId") : null);
        f.setUtilizador(rs.getString("Utilizador"));
        f.setPassword(rs.getString("Password"));
        f.setFuncao(Funcao.valueOf(rs.getString("Funcao")));
        return f;
    }
}
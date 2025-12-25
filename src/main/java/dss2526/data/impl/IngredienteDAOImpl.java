package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.IngredienteDAO;
import dss2526.domain.entity.Ingrediente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IngredienteDAOImpl implements IngredienteDAO {

    private static IngredienteDAOImpl instance;
    private final DBConfig dbConfig = DBConfig.getInstance();

    public static IngredienteDAOImpl getInstance() {
        if (instance == null) instance = new IngredienteDAOImpl();
        return instance;
    }

    private IngredienteDAOImpl() {}

    @Override
    public Ingrediente create(Ingrediente ing) {
        String sql = "INSERT INTO Ingrediente (Nome, Unidade, Alergenico) VALUES (?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, ing.getNome());
            stmt.setString(2, ing.getUnidade());
            stmt.setString(3, ing.getAlergenico());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    ing.setId(rs.getInt(1));
                }
            }
            return ing;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Ingrediente findById(Integer id) {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Ingrediente WHERE Id = ?")) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return parseIngrediente(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Ingrediente update(Ingrediente ing) {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE Ingrediente SET Nome=?, Unidade=?, Alergenico=? WHERE Id=?")) {
            
            stmt.setString(1, ing.getNome());
            stmt.setString(2, ing.getUnidade());
            stmt.setString(3, ing.getAlergenico());
            stmt.setInt(4, ing.getId());
            stmt.executeUpdate();
            return ing;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Ingrediente WHERE Id=?")) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Ingrediente> findAll() {
        List<Ingrediente> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Ingrediente");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(parseIngrediente(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Ingrediente parseIngrediente(ResultSet rs) throws SQLException {
        Ingrediente ing = new Ingrediente();
        ing.setId(rs.getInt("Id"));
        ing.setNome(rs.getString("Nome"));
        ing.setUnidade(rs.getString("Unidade"));
        ing.setAlergenico(rs.getString("Alergenico"));
        return ing;
    }
}
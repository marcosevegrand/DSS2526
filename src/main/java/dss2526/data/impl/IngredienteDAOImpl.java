package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.IngredienteDAO;
import dss2526.domain.entity.Ingrediente;

import java.sql.*;
import java.util.*;

public class IngredienteDAOImpl implements IngredienteDAO {
    private static IngredienteDAOImpl instance;
    private final DBConfig dbConfig;
    private final Map<Integer, Ingrediente> ingredienteMap = new HashMap<>();

    private IngredienteDAOImpl() { this.dbConfig = DBConfig.getInstance(); }

    public static synchronized IngredienteDAOImpl getInstance() {
        if (instance == null) instance = new IngredienteDAOImpl();
        return instance;
    }

    @Override
    public Ingrediente create(Ingrediente entity) {
        String sql = "INSERT INTO Ingrediente (nome, unidade, alergenico) VALUES (?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getNome());
            ps.setString(2, entity.getUnidade());
            ps.setString(3, entity.getAlergenico());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) { entity.setId(rs.getInt(1)); ingredienteMap.put(entity.getId(), entity); }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return entity;
    }

    @Override
    public Ingrediente update(Ingrediente entity) {
        String sql = "UPDATE Ingrediente SET nome=?, unidade=?, alergenico=? WHERE id=?";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entity.getNome()); ps.setString(2, entity.getUnidade()); ps.setString(3, entity.getAlergenico()); ps.setInt(4, entity.getId());
            ps.executeUpdate(); ingredienteMap.put(entity.getId(), entity);
        } catch (SQLException e) { e.printStackTrace(); }
        return entity;
    }

    @Override
    public Ingrediente findById(Integer id) {
        if (ingredienteMap.containsKey(id)) return ingredienteMap.get(id);
        String sql = "SELECT * FROM Ingrediente WHERE id = ?";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) { Ingrediente i = map(rs); ingredienteMap.put(i.getId(), i); return i; } }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private Ingrediente map(ResultSet rs) throws SQLException {
        Ingrediente i = new Ingrediente(); i.setId(rs.getInt("id")); i.setNome(rs.getString("nome")); i.setUnidade(rs.getString("unidade")); i.setAlergenico(rs.getString("alergenico")); return i;
    }

    @Override public List<Ingrediente> findAll() {
        List<Ingrediente> list = new ArrayList<>();
        String sql = "SELECT * FROM Ingrediente ORDER BY id";
        try (Connection conn = dbConfig.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) { int id = rs.getInt("id"); if (ingredienteMap.containsKey(id)) list.add(ingredienteMap.get(id)); else { Ingrediente i = map(rs); ingredienteMap.put(i.getId(), i); list.add(i); } }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override public boolean delete(Integer id) {
        String sql = "DELETE FROM Ingrediente WHERE id = ?";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); int rows = ps.executeUpdate(); if (rows > 0) ingredienteMap.remove(id); return rows > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
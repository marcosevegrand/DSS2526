package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.FuncionarioDAO;
import dss2526.domain.entity.Funcionario;
import dss2526.domain.enumeration.Funcao;

import java.sql.*;
import java.util.*;

public class FuncionarioDAOImpl implements FuncionarioDAO {
    private static FuncionarioDAOImpl instance;
    private final DBConfig dbConfig;
    private final Map<Integer, Funcionario> funcionarioMap = new HashMap<>();

    private FuncionarioDAOImpl() { this.dbConfig = DBConfig.getInstance(); }

    public static synchronized FuncionarioDAOImpl getInstance() {
        if (instance == null) instance = new FuncionarioDAOImpl();
        return instance;
    }

    @Override
    public Funcionario create(Funcionario entity) {
        String sql = "INSERT INTO Funcionario (restaurante_id, utilizador, password, funcao) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (entity.getRestauranteId() != null) ps.setInt(1, entity.getRestauranteId());
            else ps.setNull(1, Types.INTEGER);
            ps.setString(2, entity.getUtilizador());
            ps.setString(3, entity.getPassword());
            ps.setString(4, entity.getFuncao().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) { entity.setId(rs.getInt(1)); funcionarioMap.put(entity.getId(), entity); }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return entity;
    }

    @Override
    public Funcionario update(Funcionario entity) {
        String sql = "UPDATE Funcionario SET restaurante_id=?, utilizador=?, password=?, funcao=? WHERE id=?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (entity.getRestauranteId() != null) ps.setInt(1, entity.getRestauranteId());
            else ps.setNull(1, Types.INTEGER);
            ps.setString(2, entity.getUtilizador());
            ps.setString(3, entity.getPassword());
            ps.setString(4, entity.getFuncao().name());
            ps.setInt(5, entity.getId());
            ps.executeUpdate();
            funcionarioMap.put(entity.getId(), entity);
        } catch (SQLException e) { e.printStackTrace(); }
        return entity;
    }

    @Override
    public Funcionario findById(Integer id) {
        if (funcionarioMap.containsKey(id)) return funcionarioMap.get(id);
        String sql = "SELECT * FROM Funcionario WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { Funcionario f = map(rs); funcionarioMap.put(f.getId(), f); return f; }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private Funcionario map(ResultSet rs) throws SQLException {
        Funcionario f = new Funcionario();
        f.setId(rs.getInt("id"));
        int rId = rs.getInt("restaurante_id");
        if (!rs.wasNull()) f.setRestauranteId(rId);
        f.setUtilizador(rs.getString("utilizador"));
        f.setPassword(rs.getString("password"));
        f.setFuncao(Funcao.valueOf(rs.getString("funcao")));
        return f;
    }

    @Override public List<Funcionario> findAll() {
        List<Funcionario> list = new ArrayList<>();
        String sql = "SELECT * FROM Funcionario ORDER BY id";
        try (Connection conn = dbConfig.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) { int id = rs.getInt("id"); if (funcionarioMap.containsKey(id)) list.add(funcionarioMap.get(id)); else { Funcionario f = map(rs); funcionarioMap.put(f.getId(), f); list.add(f); } }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override public List<Funcionario> findAllByRestaurante(int restauranteId) {
        List<Funcionario> list = new ArrayList<>();
        String sql = "SELECT * FROM Funcionario WHERE restaurante_id = ? ORDER BY id";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, restauranteId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) { int id = rs.getInt("id"); if (funcionarioMap.containsKey(id)) list.add(funcionarioMap.get(id)); else { Funcionario f = map(rs); funcionarioMap.put(f.getId(), f); list.add(f); } } }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override public Funcionario findByUtilizador(String utilizador) {
        String sql = "SELECT * FROM Funcionario WHERE utilizador = ?";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, utilizador);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { int id = rs.getInt("id"); if (funcionarioMap.containsKey(id)) return funcionarioMap.get(id); Funcionario f = map(rs); funcionarioMap.put(f.getId(), f); return f; }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override public boolean delete(Integer id) {
        String sql = "DELETE FROM Funcionario WHERE id = ?";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); int rows = ps.executeUpdate(); if (rows > 0) funcionarioMap.remove(id); return rows > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
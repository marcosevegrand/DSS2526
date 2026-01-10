package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.ProdutoDAO;
import dss2526.domain.entity.LinhaProduto;
import dss2526.domain.entity.Produto;

import java.sql.*;
import java.util.*;

public class ProdutoDAOImpl implements ProdutoDAO {
    private static ProdutoDAOImpl instance;
    private final DBConfig dbConfig;
    private final Map<Integer, Produto> produtoMap = new HashMap<>();
    private final Map<Integer, LinhaProduto> linhaProdutoMap = new HashMap<>();

    private ProdutoDAOImpl() { this.dbConfig = DBConfig.getInstance(); }

    public static synchronized ProdutoDAOImpl getInstance() {
        if (instance == null) instance = new ProdutoDAOImpl();
        return instance;
    }

    @Override
    public Produto create(Produto entity) {
        String sql = "INSERT INTO Produto (nome, preco) VALUES (?, ?)";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, entity.getNome()); ps.setDouble(2, entity.getPreco()); ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) { entity.setId(rs.getInt(1)); produtoMap.put(entity.getId(), entity); } }
                insertLinhas(conn, entity);
                insertPassoIds(conn, entity);
                conn.commit();
            } catch (SQLException e) { conn.rollback(); throw e; }
        } catch (SQLException e) { e.printStackTrace(); }
        return entity;
    }

    private void insertLinhas(Connection conn, Produto entity) throws SQLException {
        String sqlLinha = "INSERT INTO LinhaProduto (produto_id, ingrediente_id, quantidade) VALUES (?, ?, ?)";
        try (PreparedStatement psL = conn.prepareStatement(sqlLinha, Statement.RETURN_GENERATED_KEYS)) {
            for (LinhaProduto lp : entity.getLinhas()) {
                psL.setInt(1, entity.getId()); psL.setInt(2, lp.getIngredienteId());
                psL.setInt(3, lp.getQuantidade()); // CORRIGIDO: setInt em vez de setDouble
                psL.executeUpdate();
                try (ResultSet rs = psL.getGeneratedKeys()) { if (rs.next()) { lp.setId(rs.getInt(1)); lp.setProdutoId(entity.getId()); linhaProdutoMap.put(lp.getId(), lp); } }
            }
        }
    }

    private void insertPassoIds(Connection conn, Produto entity) throws SQLException {
        String sqlPasso = "INSERT INTO Produto_Passo (produto_id, passo_id) VALUES (?, ?)";
        try (PreparedStatement psP = conn.prepareStatement(sqlPasso)) {
            for (Integer pId : entity.getPassoIds()) { psP.setInt(1, entity.getId()); psP.setInt(2, pId); psP.executeUpdate(); }
        }
    }

    @Override
    public Produto update(Produto entity) {
        String sql = "UPDATE Produto SET nome=?, preco=? WHERE id=?";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, entity.getNome()); ps.setDouble(2, entity.getPreco()); ps.setInt(3, entity.getId()); ps.executeUpdate();
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("DELETE FROM LinhaProduto WHERE produto_id=" + entity.getId());
                    stmt.executeUpdate("DELETE FROM Produto_Passo WHERE produto_id=" + entity.getId());
                }
                insertLinhas(conn, entity);
                insertPassoIds(conn, entity);
                conn.commit(); produtoMap.put(entity.getId(), entity);
            } catch (SQLException e) { conn.rollback(); throw e; }
        } catch (SQLException e) { e.printStackTrace(); }
        return entity;
    }

    @Override public Produto findById(Integer id) {
        if (produtoMap.containsKey(id)) return produtoMap.get(id);
        String sql = "SELECT * FROM Produto WHERE id = ?";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Produto p = new Produto(); p.setId(rs.getInt("id")); p.setNome(rs.getString("nome")); p.setPreco(rs.getDouble("preco"));
                    produtoMap.put(p.getId(), p);
                    p.setLinhas(findLinhas(conn, id));
                    p.setPassoIds(findPassoIds(conn, id));
                    return p;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private List<LinhaProduto> findLinhas(Connection conn, int prodId) throws SQLException {
        List<LinhaProduto> list = new ArrayList<>();
        String sql = "SELECT * FROM LinhaProduto WHERE produto_id = ? ORDER BY id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, prodId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    if (linhaProdutoMap.containsKey(id)) list.add(linhaProdutoMap.get(id));
                    else {
                        LinhaProduto lp = new LinhaProduto(); lp.setId(id); lp.setProdutoId(rs.getInt("produto_id")); lp.setIngredienteId(rs.getInt("ingrediente_id")); lp.setQuantidade(rs.getInt("quantidade"));
                        linhaProdutoMap.put(id, lp); list.add(lp);
                    }
                }
            }
        }
        return list;
    }

    private List<Integer> findPassoIds(Connection conn, int prodId) throws SQLException {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT passo_id FROM Produto_Passo WHERE produto_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) { ps.setInt(1, prodId); try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(rs.getInt("passo_id")); } }
        return list;
    }

    @Override public List<Produto> findAll() {
        List<Produto> list = new ArrayList<>();
        String sql = "SELECT id FROM Produto ORDER BY id";
        try (Connection conn = dbConfig.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(findById(rs.getInt("id")));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override public boolean delete(Integer id) {
        String sql = "DELETE FROM Produto WHERE id = ?";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); int rows = ps.executeUpdate(); if (rows > 0) produtoMap.remove(id); return rows > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
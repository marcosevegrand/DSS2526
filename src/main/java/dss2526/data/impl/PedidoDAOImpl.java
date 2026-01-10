package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.PedidoDAO;
import dss2526.domain.entity.LinhaPedido;
import dss2526.domain.entity.Pedido;
import dss2526.domain.enumeration.EstadoPedido;
import dss2526.domain.enumeration.TipoItem;

import java.sql.*;
import java.util.*;

public class PedidoDAOImpl implements PedidoDAO {
    private static PedidoDAOImpl instance;
    private final DBConfig dbConfig;
    private final Map<Integer, Pedido> pedidoMap = new HashMap<>();
    private final Map<Integer, LinhaPedido> linhaPedidoMap = new HashMap<>();

    private PedidoDAOImpl() { this.dbConfig = DBConfig.getInstance(); }

    public static synchronized PedidoDAOImpl getInstance() {
        if (instance == null) instance = new PedidoDAOImpl();
        return instance;
    }

    @Override
    public Pedido create(Pedido entity) {
        String sql = "INSERT INTO Pedido (restaurante_id, para_levar, estado, data_criacao) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, entity.getRestauranteId()); ps.setBoolean(2, entity.isParaLevar()); ps.setString(3, entity.getEstado().name());
                ps.setTimestamp(4, entity.getDataCriacao() != null ? Timestamp.valueOf(entity.getDataCriacao()) : new Timestamp(System.currentTimeMillis()));
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) { entity.setId(rs.getInt(1)); pedidoMap.put(entity.getId(), entity); } }
                insertLinhas(conn, entity);
                conn.commit();
            } catch (SQLException e) { conn.rollback(); throw e; }
        } catch (SQLException e) { e.printStackTrace(); }
        return entity;
    }

    private void insertLinhas(Connection conn, Pedido entity) throws SQLException {
        String sqlLinha = "INSERT INTO LinhaPedido (pedido_id, item_id, tipo, quantidade, preco_unitario, observacao) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement psL = conn.prepareStatement(sqlLinha, Statement.RETURN_GENERATED_KEYS)) {
            for (LinhaPedido lp : entity.getLinhas()) {
                psL.setInt(1, entity.getId()); psL.setInt(2, lp.getItemId()); psL.setString(3, lp.getTipo().name()); psL.setInt(4, lp.getQuantidade()); psL.setDouble(5, lp.getPrecoUnitario()); psL.setString(6, lp.getObservacao());
                psL.executeUpdate();
                try (ResultSet rs = psL.getGeneratedKeys()) { if (rs.next()) { lp.setId(rs.getInt(1)); lp.setPedidoId(entity.getId()); linhaPedidoMap.put(lp.getId(), lp); } }
            }
        }
    }

    @Override
    public Pedido update(Pedido entity) {
        String sql = "UPDATE Pedido SET restaurante_id=?, para_levar=?, estado=?, data_criacao=?, data_conclusao=? WHERE id=?";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, entity.getRestauranteId()); ps.setBoolean(2, entity.isParaLevar()); ps.setString(3, entity.getEstado().name());
                ps.setTimestamp(4, entity.getDataCriacao() != null ? Timestamp.valueOf(entity.getDataCriacao()) : new Timestamp(System.currentTimeMillis()));
                ps.setTimestamp(5, entity.getDataConclusao() != null ? Timestamp.valueOf(entity.getDataConclusao()) : null);
                ps.setInt(6, entity.getId()); ps.executeUpdate();
                try (Statement stmt = conn.createStatement()) { stmt.executeUpdate("DELETE FROM LinhaPedido WHERE pedido_id=" + entity.getId()); }
                insertLinhas(conn, entity);
                conn.commit(); pedidoMap.put(entity.getId(), entity);
            } catch (SQLException e) { conn.rollback(); throw e; }
        } catch (SQLException e) { e.printStackTrace(); }
        return entity;
    }

    @Override public Pedido findById(Integer id) {
        if (pedidoMap.containsKey(id)) return pedidoMap.get(id);
        String sql = "SELECT * FROM Pedido WHERE id = ?";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return map(rs, conn); }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private Pedido map(ResultSet rs, Connection conn) throws SQLException {
        Pedido p = new Pedido(); p.setId(rs.getInt("id")); p.setRestauranteId(rs.getInt("restaurante_id")); p.setParaLevar(rs.getBoolean("para_levar")); p.setEstado(EstadoPedido.valueOf(rs.getString("estado")));
        Timestamp tsCriacao = rs.getTimestamp("data_criacao"); p.setDataCriacao(tsCriacao != null ? tsCriacao.toLocalDateTime() : null);
        Timestamp tsConclusao = rs.getTimestamp("data_conclusao"); p.setDataConclusao(tsConclusao != null ? tsConclusao.toLocalDateTime() : null);
        pedidoMap.put(p.getId(), p);
        p.setLinhas(findLinhas(conn, p.getId()));
        return p;
    }

    private List<LinhaPedido> findLinhas(Connection conn, int pedidoId) throws SQLException {
        List<LinhaPedido> list = new ArrayList<>();
        String sql = "SELECT * FROM LinhaPedido WHERE pedido_id = ? ORDER BY id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pedidoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    if (linhaPedidoMap.containsKey(id)) list.add(linhaPedidoMap.get(id));
                    else {
                        LinhaPedido lp = new LinhaPedido(); lp.setId(id); lp.setPedidoId(rs.getInt("pedido_id")); lp.setItemId(rs.getInt("item_id")); lp.setTipo(TipoItem.valueOf(rs.getString("tipo"))); lp.setQuantidade(rs.getInt("quantidade")); lp.setPrecoUnitario(rs.getDouble("preco_unitario")); lp.setObservacao(rs.getString("observacao"));
                        linhaPedidoMap.put(id, lp); list.add(lp);
                    }
                }
            }
        }
        return list;
    }

    @Override public List<Pedido> findAll() {
        List<Pedido> list = new ArrayList<>();
        String sql = "SELECT * FROM Pedido ORDER BY id";
        try (Connection conn = dbConfig.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) { int id = rs.getInt("id"); if (pedidoMap.containsKey(id)) list.add(pedidoMap.get(id)); else list.add(map(rs, conn)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override public List<Pedido> findAllByRestaurante(int restauranteId) {
        List<Pedido> list = new ArrayList<>();
        String sql = "SELECT * FROM Pedido WHERE restaurante_id = ? ORDER BY id";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, restauranteId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) { int id = rs.getInt("id"); if (pedidoMap.containsKey(id)) list.add(pedidoMap.get(id)); else list.add(map(rs, conn)); } }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override public boolean delete(Integer id) {
        String sql = "DELETE FROM Pedido WHERE id = ?";
        try (Connection conn = dbConfig.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); int rows = ps.executeUpdate(); if (rows > 0) pedidoMap.remove(id); return rows > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
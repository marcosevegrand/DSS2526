package dss2526.data.impl;

import dss2526.domain.entity.Pedido;
import dss2526.domain.entity.LinhaPedido;
import dss2526.domain.entity.Produto; // Assuming Items are mostly products or we handle polymorphic
import dss2526.domain.enumeration.EstadoPedido;
import dss2526.data.config.DBConfig;
import dss2526.data.contract.PedidoDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAOImpl implements PedidoDAO {
    // Note: Handling polymorphic 'Item' in LinhaPedido is complex in pure JDBC
    // without a clean schema.
    // I will assume Item is primarily Produto for simplicity or check a type
    // column.

    @Override
    public void put(Integer key, Pedido value) {
        try (Connection conn = DBConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Upsert Pedido
                boolean exists = containsKey(conn, key);
                if (exists) {
                    try (PreparedStatement ps = conn
                            .prepareStatement("UPDATE pedidos SET para_levar=?, estado=?, data_hora=? WHERE id=?")) {
                        ps.setBoolean(1, value.isParaLevar());
                        ps.setString(2, value.getEstado().name());
                        ps.setTimestamp(3, Timestamp.valueOf(value.getDataHora()));
                        ps.setInt(4, key);
                        ps.executeUpdate();
                    }
                    // Delete lines
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM linha_pedido WHERE pedido_id=?")) {
                        ps.setInt(1, key);
                        ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO pedidos (id, para_levar, estado, data_hora) VALUES (?, ?, ?, ?)")) {
                        ps.setInt(1, key);
                        ps.setBoolean(2, value.isParaLevar());
                        ps.setString(3, value.getEstado().name());
                        ps.setTimestamp(4, Timestamp.valueOf(value.getDataHora()));
                        ps.executeUpdate();
                    }
                }

                // Insert lines
                if (value.getLinhasPedido() != null) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO linha_pedido (pedido_id, item_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)")) {
                        for (LinhaPedido lp : value.getLinhasPedido()) {
                            ps.setInt(1, key);
                            // Assuming Item has ID. Null check if item is just an abstract thing?
                            // We need to know if it's a Menu or Produto to be 100% correct, but schema
                            // usually simplifies to item_id.
                            // I'll assume item_id refers to a generic items table or similar, OR we act as
                            // if it's product.
                            ps.setInt(2, lp.getItem().getId());
                            ps.setInt(3, lp.getQuantidade());
                            ps.setBigDecimal(4, lp.getPrecoUnitario());
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao guardar pedido", e);
        }
    }

    @Override
    public Pedido save(Pedido value) {
        if (value.getId() != null && value.getId() != 0) {
            put(value.getId(), value);
            return value;
        }
        try (Connection conn = DBConfig.getConnection()) {
            int newId = 1;
            try (Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery("SELECT MAX(id) FROM pedidos")) {
                if (rs.next()) {
                    newId = rs.getInt(1) + 1;
                }
            }
            value.setId(newId);
            put(newId, value);
            return value;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao gerar ID para pedido", e);
        }
    }

    private boolean containsKey(Connection conn, Integer key) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM pedidos WHERE id=?")) {
            ps.setInt(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public Pedido get(Integer key) {
        Pedido p = null;
        try (Connection conn = DBConfig.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM pedidos WHERE id=?")) {
                ps.setInt(1, key);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        p = buildPedidoSimple(rs);
                    }
                }
            }
            if (p != null) {
                loadLines(conn, p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter pedido", e);
        }
        return p;
    }

    private void loadLines(Connection conn, Pedido p) throws SQLException {
        String sql = "SELECT * FROM linha_pedido WHERE pedido_id = ?";

        // 1. Declarar a lista FORA do bloco try do ResultSet
        List<LinhaPedido> lines = new ArrayList<>(); 

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Criar o item (neste caso Produto como base)
                    Produto prod = new Produto();
                    prod.setId(rs.getInt("item_id"));

                    // 2. Usar o construtor de 4 argumentos que criámos na LinhaPedido
                    LinhaPedido lp = new LinhaPedido(
                        prod, 
                        rs.getInt("quantidade"), 
                        rs.getBigDecimal("preco_unitario"),
                        rs.getString("observacao") // <--- Lê a nota da BD
                    );

                    lp.setId(rs.getInt("id"));
                    lines.add(lp);
                }
            } 
        } 

        p.setLinhasPedido(lines);
    }
    
    @Override
    public Pedido remove(Integer key) {
        Pedido p = get(key);
        if (p != null) {
            try (Connection conn = DBConfig.getConnection()) {
                conn.setAutoCommit(false);
                try {
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM linha_pedido WHERE pedido_id=?")) {
                        ps.setInt(1, key);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM pedidos WHERE id=?")) {
                        ps.setInt(1, key);
                        ps.executeUpdate();
                    }
                    conn.commit();
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao remover pedido", e);
            }
        }
        return p;
    }

    @Override
    public boolean containsKey(Integer key) {
        try (Connection conn = DBConfig.getConnection()) {
            return containsKey(conn, key);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Pedido> values() {
        List<Pedido> list = new ArrayList<>();
        try (Connection conn = DBConfig.getConnection()) {
            try (Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery("SELECT * FROM pedidos")) {
                while (rs.next()) {
                    list.add(buildPedidoSimple(rs));
                }
            }
            // Optional: load lines. For heavy stats, maybe we avoid it or do it batch.
            // But API says 'values' returns full objects.
            for (Pedido p : list) {
                loadLines(conn, p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos", e);
        }
        return list;
    }

    @Override
    public int size() {
        try (Connection conn = DBConfig.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT count(*) FROM pedidos")) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public List<Pedido> findByEstado(EstadoPedido estado) {
        List<Pedido> list = new ArrayList<>();
        try (Connection conn = DBConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM pedidos WHERE estado=?")) {
            ps.setString(1, estado.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(buildPedidoSimple(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pedidos por estado", e);
        }
        return list;
    }

    @Override
    public List<Pedido> findByData(java.time.LocalDate data) {
        List<Pedido> list = new ArrayList<>();
        // Assuming data_hora is TIMESTAMP. casting to DATE
        try (Connection conn = DBConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM pedidos WHERE DATE(data_hora) = ?")) {
            ps.setDate(1, java.sql.Date.valueOf(data));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(buildPedidoSimple(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pedidos por data", e);
        }
        return list;
    }

    @Override
    public java.util.Optional<Pedido> findUltimoPorTerminal(int idTerminal) {
        // Stub: Pedido entity does not track terminal ID currently.
        return java.util.Optional.empty();
    }

    private Pedido buildPedidoSimple(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setId(rs.getInt("id"));
        p.setParaLevar(rs.getBoolean("para_levar"));
        p.setEstado(EstadoPedido.valueOf(rs.getString("estado")));
        p.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
        p.setNotaGeral(rs.getString("nota_geral"));
        return p;
    }
}
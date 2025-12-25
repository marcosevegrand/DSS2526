package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.PedidoDAO;
import dss2526.domain.entity.LinhaPedido;
import dss2526.domain.entity.Pedido;
import dss2526.domain.enumeration.EstadoPedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAOImpl implements PedidoDAO {
    private static PedidoDAOImpl instance;
    private final DBConfig dbConfig = DBConfig.getInstance();

    public static PedidoDAOImpl getInstance() {
        if(instance == null) instance = new PedidoDAOImpl();
        return instance;
    }

    private PedidoDAOImpl() {}

    @Override
    public Pedido create(Pedido p) {
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            String sql = "INSERT INTO Pedido (RestauranteId, Estado, DataHora, ParaLevar) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, p.getRestauranteId());
                stmt.setString(2, p.getEstado().name());
                stmt.setTimestamp(3, Timestamp.valueOf(p.getDataHora()));
                stmt.setBoolean(4, p.isParaLevar());
                stmt.executeUpdate();
                
                try(ResultSet rs = stmt.getGeneratedKeys()){
                    if(rs.next()) p.setId(rs.getInt(1));
                }
            }

            if (p.getLinhasPedido() != null) {
                String sqlL = "INSERT INTO LinhaPedido (PedidoId, ItemId, Quantidade, PrecoUnitario, Observacao) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmtL = conn.prepareStatement(sqlL)) {
                    for (LinhaPedido lp : p.getLinhasPedido()) {
                        stmtL.setInt(1, p.getId());
                        stmtL.setInt(2, lp.getIdItem());
                        stmtL.setInt(3, lp.getQuantidade());
                        stmtL.setDouble(4, lp.getPrecoUnitario());
                        stmtL.setString(5, lp.getObservacao());
                        stmtL.addBatch();
                    }
                    stmtL.executeBatch();
                }
            }

            conn.commit();
            return p;
        } catch (SQLException e) {
            if(conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return null;
        } finally {
            if(conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public Pedido findById(Integer id) {
        Pedido p = null;
        try (Connection conn = dbConfig.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Pedido WHERE Id=?")) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if(rs.next()) {
                        p = new Pedido();
                        p.setId(rs.getInt("Id"));
                        p.setRestauranteId(rs.getInt("RestauranteId"));
                        p.setEstado(EstadoPedido.valueOf(rs.getString("Estado")));
                        p.setDataHora(rs.getTimestamp("DataHora").toLocalDateTime());
                        p.setParaLevar(rs.getBoolean("ParaLevar"));
                    }
                }
            }

            if (p != null) {
                carregarLinhas(conn, p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }

    @Override
    public Pedido update(Pedido p) {
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement("UPDATE Pedido SET RestauranteId=?, Estado=?, DataHora=?, ParaLevar=? WHERE Id=?")) {
                stmt.setInt(1, p.getRestauranteId());
                stmt.setString(2, p.getEstado().name());
                stmt.setTimestamp(3, Timestamp.valueOf(p.getDataHora()));
                stmt.setBoolean(4, p.isParaLevar());
                stmt.setInt(5, p.getId());
                stmt.executeUpdate();
            }

            try (PreparedStatement stmtDel = conn.prepareStatement("DELETE FROM LinhaPedido WHERE PedidoId=?")) {
                stmtDel.setInt(1, p.getId());
                stmtDel.executeUpdate();
            }

            if (p.getLinhasPedido() != null && !p.getLinhasPedido().isEmpty()) {
                String sqlL = "INSERT INTO LinhaPedido (PedidoId, ItemId, Quantidade, PrecoUnitario, Observacao) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmtL = conn.prepareStatement(sqlL)) {
                    for (LinhaPedido lp : p.getLinhasPedido()) {
                        stmtL.setInt(1, p.getId());
                        stmtL.setInt(2, lp.getIdItem());
                        stmtL.setInt(3, lp.getQuantidade());
                        stmtL.setDouble(4, lp.getPrecoUnitario());
                        stmtL.setString(5, lp.getObservacao());
                        stmtL.addBatch();
                    }
                    stmtL.executeBatch();
                }
            }

            conn.commit();
            return p;
        } catch (SQLException e) {
            if(conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return null;
        } finally {
            if(conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public boolean delete(Integer id) {
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM LinhaPedido WHERE PedidoId=?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            
            int rows;
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Pedido WHERE Id=?")) {
                stmt.setInt(1, id);
                rows = stmt.executeUpdate();
            }
            
            conn.commit();
            return rows > 0;
        } catch (SQLException e) {
            if(conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if(conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public List<Pedido> findAll() {
        List<Pedido> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT Id FROM Pedido");
             ResultSet rs = stmt.executeQuery()) {
            while(rs.next()) {
                list.add(findById(rs.getInt("Id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Pedido> findByRestaurante(int restauranteId) {
        List<Pedido> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT Id FROM Pedido WHERE RestauranteId=?")) {
            stmt.setInt(1, restauranteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    // Reutilizamos findById para garantir que a composição é carregada corretamente
                    list.add(findById(rs.getInt("Id")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void carregarLinhas(Connection conn, Pedido p) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM LinhaPedido WHERE PedidoId=?")) {
            stmt.setInt(1, p.getId());
            List<LinhaPedido> linhas = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    LinhaPedido lp = new LinhaPedido();
                    lp.setId(rs.getInt("Id"));
                    lp.setIdItem(rs.getInt("ItemId"));
                    lp.setQuantidade(rs.getInt("Quantidade"));
                    lp.setPrecoUnitario(rs.getDouble("PrecoUnitario"));
                    lp.setObservacao(rs.getString("Observacao"));
                    linhas.add(lp);
                }
            }
            p.setLinhasPedido(linhas);
        }
    }
}
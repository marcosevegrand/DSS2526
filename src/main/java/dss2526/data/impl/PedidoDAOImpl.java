package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.PedidoDAO;
import dss2526.domain.contract.Item;
import dss2526.domain.entity.*;
import dss2526.domain.enumeration.EstadoPedido;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class PedidoDAOImpl implements PedidoDAO {

    private DBConfig dbConfig = DBConfig.getInstance();
    private static Map<Integer, Pedido> identityMap = new HashMap<>();

    @Override
    public Pedido save(Pedido p) {
        String sql = "INSERT INTO Pedido (Estado, DataHora, ParaLevar) VALUES (?, ?, ?)";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, p.getEstado().name());
                ps.setTimestamp(2, Timestamp.valueOf(p.getDataHora()));
                ps.setBoolean(3, p.isParaLevar());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) p.setId(rs.getInt(1));
                }
                saveLines(conn, p);
                conn.commit();
                identityMap.put(p.getId(), p);
            } catch (SQLException e) { conn.rollback(); throw e; }
        } catch (SQLException e) { e.printStackTrace(); }
        return p;
    }

    @Override
    public Pedido update(Pedido p) {
        String sql = "UPDATE Pedido SET Estado = ?, ParaLevar = ? WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, p.getEstado().name());
                ps.setBoolean(2, p.isParaLevar());
                ps.setInt(3, p.getId());
                ps.executeUpdate();

                try (PreparedStatement del = conn.prepareStatement("DELETE FROM LinhaPedido WHERE PedidoID = ?")) {
                    del.setInt(1, p.getId()); del.executeUpdate();
                }
                saveLines(conn, p);
                conn.commit();
                identityMap.put(p.getId(), p);
            } catch (SQLException e) { conn.rollback(); throw e; }
        } catch (SQLException e) { e.printStackTrace(); }
        return p;
    }

    private void saveLines(Connection conn, Pedido p) throws SQLException {
        if (p.getLinhasPedido() != null) {
            String sql = "INSERT INTO LinhaPedido (PedidoID, ItemID, ItemTipo, Quantidade, PrecoUnitario, Observacao) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (LinhaPedido lp : p.getLinhasPedido()) {
                    ps.setInt(1, p.getId());
                    ps.setInt(2, lp.getItem().getId());
                    ps.setString(3, (lp.getItem() instanceof Menu) ? "MENU" : "PRODUTO");
                    ps.setInt(4, lp.getQuantidade());
                    ps.setDouble(5, lp.getPrecoUnitario());
                    ps.setString(6, lp.getObservacao());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    @Override
    public List<Pedido> findByData(LocalDate data) {
        List<Pedido> res = new ArrayList<>();
        // Filtra pela data (ignorando a hora no Timestamp)
        String sql = "SELECT ID FROM Pedido WHERE DATE(DataHora) = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(data));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    res.add(findById(rs.getInt("ID")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return res;
    }


    @Override
    public Pedido findById(Integer id) {
        if (identityMap.containsKey(id)) return identityMap.get(id);
        String sql = "SELECT * FROM Pedido WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Pedido p = new Pedido();
                    p.setId(rs.getInt("ID"));
                    p.setEstado(EstadoPedido.valueOf(rs.getString("Estado")));
                    p.setDataHora(rs.getTimestamp("DataHora").toLocalDateTime());
                    p.setParaLevar(rs.getBoolean("ParaLevar"));
                    identityMap.put(p.getId(), p);
                    loadLines(conn, p);
                    return p;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private void loadLines(Connection conn, Pedido p) throws SQLException {
        List<LinhaPedido> list = new ArrayList<>();
        String sql = "SELECT ItemID, ItemTipo, Quantidade, PrecoUnitario, Observacao FROM LinhaPedido WHERE PedidoID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getId());
            ResultSet rs = ps.executeQuery();
            ProdutoDAOImpl pDao = new ProdutoDAOImpl();
            MenuDAOImpl mDao = new MenuDAOImpl();
            while (rs.next()) {
                Item item = rs.getString("ItemTipo").equals("MENU") ? mDao.findById(rs.getInt(1)) : pDao.findById(rs.getInt(1));
                list.add(new LinhaPedido(item, rs.getInt(3), rs.getDouble(4), rs.getString(5)));
            }
        }
        p.setLinhasPedido(list);
    }

    @Override public List<Pedido> findAll() {
        List<Pedido> res = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection(); Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT ID FROM Pedido")) {
            while (rs.next()) res.add(findById(rs.getInt(1)));
        } catch (SQLException e) { e.printStackTrace(); }
        return res;
    }

    @Override public boolean delete(Integer id) { identityMap.remove(id); return true; }
}
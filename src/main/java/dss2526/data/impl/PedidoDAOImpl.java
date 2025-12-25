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

    // --- Implementação do DELETE (O que faltava) ---
    @Override
    public boolean delete(Integer id) {
        if (id == null || id <= 0) return false;
        
        String sql = "DELETE FROM Pedido WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Primeiro removemos as linhas (Chaves Estrangeiras)
                try (PreparedStatement psLines = conn.prepareStatement("DELETE FROM LinhaPedido WHERE PedidoID = ?")) {
                    psLines.setInt(1, id);
                    psLines.executeUpdate();
                }
                // Depois removemos o pedido
                try (PreparedStatement psPedido = conn.prepareStatement(sql)) {
                    psPedido.setInt(1, id);
                    int rows = psPedido.executeUpdate();
                    
                    conn.commit();
                    identityMap.remove(id);
                    return rows > 0;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Métodos de Procura ---
    @Override
    public List<Pedido> findAll() {
        List<Pedido> res = new ArrayList<>();
        String sql = "SELECT ID FROM Pedido";
        try (Connection conn = dbConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                res.add(findById(rs.getInt("ID")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public List<Pedido> findByData(LocalDate data) {
        List<Pedido> res = new ArrayList<>();
        String sql = "SELECT ID FROM Pedido WHERE DATE(DataHora) = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(data));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    res.add(findById(rs.getInt("ID")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    // --- Métodos de Escrita ---
    @Override
    public Pedido save(Pedido p) {
        String sql = "INSERT INTO Pedido (Estado, DataHora, ParaLevar, RestauranteID) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, p.getEstado().name());
                ps.setTimestamp(2, Timestamp.valueOf(p.getDataHora()));
                ps.setBoolean(3, p.isParaLevar());
                ps.setInt(4, p.getRestauranteId());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) p.setId(rs.getInt(1));
                }

                saveLines(conn, p);
                conn.commit();
                identityMap.put(p.getId(), p);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }

    @Override
    public Pedido update(Pedido p) {
        String sql = "UPDATE Pedido SET Estado = ?, ParaLevar = ?, RestauranteID = ? WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, p.getEstado().name());
                ps.setBoolean(2, p.isParaLevar());
                ps.setInt(3, p.getRestauranteId());
                ps.setInt(4, p.getId());
                ps.executeUpdate();

                try (PreparedStatement del = conn.prepareStatement("DELETE FROM LinhaPedido WHERE PedidoID = ?")) {
                    del.setInt(1, p.getId());
                    del.executeUpdate();
                }
                
                saveLines(conn, p);
                conn.commit();
                identityMap.put(p.getId(), p);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }

    private void saveLines(Connection conn, Pedido p) throws SQLException {
        if (p.getLinhasPedido() == null || p.getLinhasPedido().isEmpty()) return;

        String sql = "INSERT INTO LinhaPedido (PedidoID, ItemID, ItemTipo, Quantidade, PrecoUnitario, Observacao) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (LinhaPedido lp : p.getLinhasPedido()) {
                ps.setInt(1, p.getId());
                ps.setInt(2, lp.getItem().getId());
                ps.setString(3, (lp.getItem() instanceof Menu) ? "MENU" : "PRODUTO");
                ps.setInt(4, lp.getQuantidade());
                ps.setDouble(5, lp.getItem().getPreco());
                ps.setString(6, lp.getObservacao());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    @Override
    public Pedido findById(Integer id) {
        if (id == null || id <= 0) return null;
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
                    p.setRestauranteId(rs.getInt("RestauranteID"));
                    
                    identityMap.put(p.getId(), p);
                    loadLines(conn, p);
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void loadLines(Connection conn, Pedido p) throws SQLException {
        List<LinhaPedido> list = new ArrayList<>();
        String sql = "SELECT ItemID, ItemTipo, Quantidade, PrecoUnitario, Observacao FROM LinhaPedido WHERE PedidoID = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getId());
            try (ResultSet rs = ps.executeQuery()) {
                ProdutoDAOImpl pDao = new ProdutoDAOImpl();
                MenuDAOImpl mDao = new MenuDAOImpl();
                
                while (rs.next()) {
                    int itemId = rs.getInt("ItemID");
                    String tipo = rs.getString("ItemTipo");
                    Item item = tipo.equals("MENU") ? mDao.findById(itemId) : pDao.findById(itemId);
                    if (item != null) {
                        list.add(new LinhaPedido(item, rs.getInt("Quantidade"), rs.getDouble("PrecoUnitario"), rs.getString("Observacao")));
                    }
                }
            }
        }
        p.setLinhasPedido(list);
    }
}
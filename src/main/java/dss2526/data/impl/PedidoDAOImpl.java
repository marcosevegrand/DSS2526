package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.PedidoDAO;
import dss2526.domain.entity.Pedido;
import dss2526.domain.enumeration.EstadoPedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PedidoDAOImpl implements PedidoDAO {

    private DBConfig dbConfig = DBConfig.getInstance();
    private static Map<Integer, Pedido> identityMap = new HashMap<>();

    @Override
    public Pedido save(Pedido p) {
        String sql = (p.getId() > 0) ?
            "INSERT INTO Pedidos (ID, Restaurante_ID, Estado, DataHora, Para_Levar) VALUES (?, ?, ?, ?, ?)" :
            "INSERT INTO Pedidos (Restaurante_ID, Estado, DataHora, Para_Levar) VALUES (?, ?, ?, ?)";

        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            int idx = 1;
            if (p.getId() > 0) pstmt.setInt(idx++, p.getId());
            pstmt.setInt(idx++, p.getRestauranteId());
            pstmt.setString(idx++, p.getEstado().name());
            pstmt.setTimestamp(idx++, Timestamp.valueOf(p.getDataHora()));
            pstmt.setBoolean(idx++, p.isParaLevar());
            
            pstmt.executeUpdate();
            
            if (p.getId() == 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) p.setId(rs.getInt(1));
                }
            }
            identityMap.put(p.getId(), p);
            return p;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Pedido findById(Integer id) {
        if (identityMap.containsKey(id)) return identityMap.get(id);

        String sql = "SELECT * FROM Pedidos WHERE ID = ?";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Pedido p = new Pedido();
                    p.setId(rs.getInt("ID"));
                    p.setRestauranteId(rs.getInt("Restaurante_ID"));
                    p.setEstado(EstadoPedido.valueOf(rs.getString("Estado")));
                    p.setDataHora(rs.getTimestamp("DataHora").toLocalDateTime());
                    p.setParaLevar(rs.getBoolean("Para_Levar"));
                    // carregar linhasPedido aqui se necessario
                    identityMap.put(p.getId(), p);
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Pedido> findAll() {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT ID FROM Pedidos";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) lista.add(findById(rs.getInt("ID")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Pedido update(Pedido p) {
        String sql = "UPDATE Pedidos SET Restaurante_ID = ?, Estado = ?, DataHora = ?, Para_Levar = ? WHERE ID = ?";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, p.getRestauranteId());
            pstmt.setString(2, p.getEstado().name());
            pstmt.setTimestamp(3, Timestamp.valueOf(p.getDataHora()));
            pstmt.setBoolean(4, p.isParaLevar());
            pstmt.setInt(5, p.getId());
            pstmt.executeUpdate();
            identityMap.put(p.getId(), p);
            return p;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Pedidos WHERE ID = ?";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            if (pstmt.executeUpdate() > 0) {
                identityMap.remove(id);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Pedido> getPendentes() {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT ID FROM Pedidos WHERE Estado != 'PAGO' AND Estado != 'ENTREGUE'"; 
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) lista.add(findById(rs.getInt("ID")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.PagamentoDAO;
import dss2526.domain.entity.Pagamento;
import dss2526.domain.enumeration.TipoPagamento;

import java.sql.*;
import java.util.*;

/**
 * Implementation of the PagamentoDAO using JDBC.
 * Follows the Singleton pattern and maintains an Identity Map for performance and consistency.
 */
public class PagamentoDAOImpl implements PagamentoDAO {
    private static PagamentoDAOImpl instance;
    private final DBConfig dbConfig;

    // Identity Map for Pagamento entity
    private final Map<Integer, Pagamento> pagamentoMap = new HashMap<>();

    private PagamentoDAOImpl() {
        this.dbConfig = DBConfig.getInstance();
    }

    public static synchronized PagamentoDAOImpl getInstance() {
        if (instance == null) {
            instance = new PagamentoDAOImpl();
        }
        return instance;
    }

    @Override
    public Pagamento create(Pagamento entity) {
        String sql = "INSERT INTO Pagamento (pedido_id, valor, tipo, confirmado, data_pagamento) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, entity.getPedidoId());
            ps.setDouble(2, entity.getValor());
            ps.setString(3, entity.getTipo().name());
            ps.setBoolean(4, entity.isConfirmado());
            ps.setTimestamp(5, entity.getData() != null ? Timestamp.valueOf(entity.getData()) : new Timestamp(System.currentTimeMillis()));
            
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getInt(1));
                    pagamentoMap.put(entity.getId(), entity);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Pagamento update(Pagamento entity) {
        String sql = "UPDATE Pagamento SET pedido_id=?, valor=?, tipo=?, confirmado=?, data_pagamento=? WHERE id=?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, entity.getPedidoId());
            ps.setDouble(2, entity.getValor());
            ps.setString(3, entity.getTipo().name());
            ps.setBoolean(4, entity.isConfirmado());
            ps.setTimestamp(5, entity.getData() != null ? Timestamp.valueOf(entity.getData()) : null);
            ps.setInt(6, entity.getId());
            
            ps.executeUpdate();
            pagamentoMap.put(entity.getId(), entity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Pagamento findById(Integer id) {
        // Check Identity Map first
        if (pagamentoMap.containsKey(id)) {
            return pagamentoMap.get(id);
        }

        String sql = "SELECT * FROM Pagamento WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Pagamento p = map(rs);
                    pagamentoMap.put(p.getId(), p);
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Pagamento> findAll() {
        List<Pagamento> list = new ArrayList<>();
        String sql = "SELECT * FROM Pagamento ORDER BY id";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                if (pagamentoMap.containsKey(id)) {
                    list.add(pagamentoMap.get(id));
                } else {
                    Pagamento p = map(rs);
                    pagamentoMap.put(p.getId(), p);
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Pagamento WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                pagamentoMap.remove(id);
            }
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper method to map a ResultSet row to a Pagamento entity.
     */
    private Pagamento map(ResultSet rs) throws SQLException {
        Pagamento p = new Pagamento();
        p.setId(rs.getInt("id"));
        p.setPedidoId(rs.getInt("pedido_id"));
        p.setValor(rs.getDouble("valor"));
        
        String tipoStr = rs.getString("tipo");
        if (tipoStr != null) {
            p.setTipo(TipoPagamento.valueOf(tipoStr));
        }
        
        p.setConfirmado(rs.getBoolean("confirmado"));
        
        Timestamp ts = rs.getTimestamp("data_pagamento");
        if (ts != null) {
            p.setData(ts.toLocalDateTime());
        }
        
        return p;
    }
}
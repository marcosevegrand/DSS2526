package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.EstacaoDAO;
import dss2526.domain.entity.*;
import dss2526.domain.enumeration.Trabalho;

import java.sql.*;
import java.util.*;

public class EstacaoDAOImpl implements EstacaoDAO {

    private DBConfig dbConfig = DBConfig.getInstance();
    private static Map<Integer, Estacao> identityMap = new HashMap<>();

    @Override
    public Estacao save(Estacao e) {
        String sql = "INSERT INTO Estacao (Trabalho) VALUES (?)";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, e.getTrabalho().name());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) e.setId(rs.getInt(1));
                }
                saveLines(conn, e);
                conn.commit();
                identityMap.put(e.getId(), e);
            } catch (SQLException ex) { conn.rollback(); throw ex; }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return e;
    }

    @Override
    public Estacao update(Estacao e) {
        String sql = "UPDATE Estacao SET Trabalho = ? WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, e.getTrabalho().name());
                ps.setInt(2, e.getId());
                ps.executeUpdate();

                try (PreparedStatement del = conn.prepareStatement("DELETE FROM LinhaEstacao WHERE EstacaoID = ?")) {
                    del.setInt(1, e.getId()); del.executeUpdate();
                }
                saveLines(conn, e);
                conn.commit();
                identityMap.put(e.getId(), e);
            } catch (SQLException ex) { conn.rollback(); throw ex; }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return e;
    }

    private void saveLines(Connection conn, Estacao e) throws SQLException {
        if (e.getLinhaEstacaos() != null) {
            String sql = "INSERT INTO LinhaEstacao (EstacaoID, TarefaID, PedidoID, Concluido) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (LinhaEstacao le : e.getLinhaEstacaos()) {
                    ps.setInt(1, e.getId());
                    ps.setInt(2, le.getTarefa().getId());
                    ps.setInt(3, le.getPedido().getId());
                    ps.setBoolean(4, le.isConcluido());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    @Override
    public Estacao findById(Integer id) {
        if (identityMap.containsKey(id)) return identityMap.get(id);
        String sql = "SELECT * FROM Estacao WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Estacao e = new Estacao();
                    e.setId(rs.getInt("ID"));
                    e.setTrabalho(Trabalho.valueOf(rs.getString("Trabalho")));
                    identityMap.put(e.getId(), e);
                    loadLines(conn, e);
                    return e;
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return null;
    }

    private void loadLines(Connection conn, Estacao e) throws SQLException {
        List<LinhaEstacao> list = new ArrayList<>();
        String sql = "SELECT TarefaID, PedidoID, Concluido FROM LinhaEstacao WHERE EstacaoID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, e.getId());
            ResultSet rs = ps.executeQuery();
            TarefaDAOImpl tDao = new TarefaDAOImpl();
            PedidoDAOImpl pDao = new PedidoDAOImpl();
            while (rs.next()) {
                list.add(new LinhaEstacao(tDao.findById(rs.getInt(1)), pDao.findById(rs.getInt(2)), rs.getBoolean(3)));
            }
        }
        e.setLinhaEstacaos(list);
    }

    @Override public List<Estacao> findAll() {
        List<Estacao> res = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection(); Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT ID FROM Estacao")) {
            while (rs.next()) res.add(findById(rs.getInt(1)));
        } catch (SQLException e) { e.printStackTrace(); }
        return res;
    }

    @Override public boolean delete(Integer id) { identityMap.remove(id); return true; }
}
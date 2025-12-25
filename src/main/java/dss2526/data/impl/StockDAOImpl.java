package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.StockDAO;
import dss2526.domain.entity.*;

import java.sql.*;
import java.util.*;

public class StockDAOImpl implements StockDAO {

    private DBConfig dbConfig = DBConfig.getInstance();
    private static Map<Integer, Stock> identityMap = new HashMap<>();

    @Override
    public Stock save(Stock s) {
        String sql = "INSERT INTO Stock (ID) VALUES (DEFAULT)";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (Statement st = conn.createStatement()) {
                st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) s.setId(rs.getInt(1));
                }
                saveLines(conn, s);
                conn.commit();
                identityMap.put(s.getId(), s);
            } catch (SQLException e) { conn.rollback(); throw e; }
        } catch (SQLException e) { e.printStackTrace(); }
        return s;
    }

    @Override
    public Stock update(Stock s) {
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement del = conn.prepareStatement("DELETE FROM LinhaStock WHERE StockID = ?")) {
                    del.setInt(1, s.getId()); del.executeUpdate();
                }
                saveLines(conn, s);
                conn.commit();
                identityMap.put(s.getId(), s);
            } catch (SQLException e) { conn.rollback(); throw e; }
        } catch (SQLException e) { e.printStackTrace(); }
        return s;
    }

    private void saveLines(Connection conn, Stock s) throws SQLException {
        if (s.getIngredientes() != null) {
            String sql = "INSERT INTO LinhaStock (StockID, IngredienteID, Quantidade) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (LinhaStock ls : s.getIngredientes()) {
                    ps.setInt(1, s.getId());
                    ps.setInt(2, ls.getIngrediente().getId());
                    ps.setDouble(3, ls.getQuantidade());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    @Override
    public Stock findById(Integer id) {
        if (identityMap.containsKey(id)) return identityMap.get(id);
        Stock s = new Stock();
        s.setId(id);
        try (Connection conn = dbConfig.getConnection()) {
            List<LinhaStock> list = new ArrayList<>();
            String sql = "SELECT IngredienteID, Quantidade FROM LinhaStock WHERE StockID = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                IngredienteDAOImpl iDao = new IngredienteDAOImpl();
                while (rs.next()) list.add(new LinhaStock(iDao.findById(rs.getInt(1)), rs.getDouble(2)));
            }
            s.setIngredientes(list);
            identityMap.put(id, s);
            return s;
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override public List<Stock> findAll() { return new ArrayList<>(); }
    @Override public boolean delete(Integer id) { identityMap.remove(id); return false; }
}
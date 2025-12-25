package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.MenuDAO;
import dss2526.domain.entity.*;

import java.sql.*;
import java.util.*;

public class MenuDAOImpl implements MenuDAO {

    private DBConfig dbConfig = DBConfig.getInstance();
    private static Map<Integer, Menu> identityMap = new HashMap<>();

    @Override
    public Menu save(Menu m) {
        String sql = "INSERT INTO Menu (Nome, Preco, Disponivel) VALUES (?, ?, ?)";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, m.getNome());
                ps.setDouble(2, m.getPreco());
                ps.setBoolean(3, m.isDisponivel());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) m.setId(rs.getInt(1));
                }
                saveLines(conn, m);
                conn.commit();
                identityMap.put(m.getId(), m);
            } catch (SQLException e) { conn.rollback(); throw e; }
        } catch (SQLException e) { e.printStackTrace(); }
        return m;
    }

    @Override
    public Menu update(Menu m) {
        String sql = "UPDATE Menu SET Nome = ?, Preco = ?, Disponivel = ? WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, m.getNome());
                ps.setDouble(2, m.getPreco());
                ps.setBoolean(3, m.isDisponivel());
                ps.setInt(4, m.getId());
                ps.executeUpdate();

                try (PreparedStatement del = conn.prepareStatement("DELETE FROM LinhaMenu WHERE MenuID = ?")) {
                    del.setInt(1, m.getId()); del.executeUpdate();
                }
                saveLines(conn, m);
                conn.commit();
                identityMap.put(m.getId(), m);
            } catch (SQLException e) { conn.rollback(); throw e; }
        } catch (SQLException e) { e.printStackTrace(); }
        return m;
    }

    private void saveLines(Connection conn, Menu m) throws SQLException {
        if (m.getLinhasMenu() != null) {
            String sql = "INSERT INTO LinhaMenu (MenuID, ProdutoID, Quantidade) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (LinhaMenu lm : m.getLinhasMenu()) {
                    ps.setInt(1, m.getId());
                    ps.setInt(2, lm.getProduto().getId());
                    ps.setInt(3, lm.getQuantidade());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    @Override
    public Menu findById(Integer id) {
        if (identityMap.containsKey(id)) return identityMap.get(id);
        String sql = "SELECT * FROM Menu WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Menu m = new Menu();
                    m.setId(rs.getInt("ID"));
                    m.setNome(rs.getString("Nome"));
                    m.setPreco(rs.getDouble("Preco"));
                    m.setDisponivel(rs.getBoolean("Disponivel"));
                    identityMap.put(m.getId(), m);
                    loadLines(conn, m);
                    return m;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private void loadLines(Connection conn, Menu m) throws SQLException {
        List<LinhaMenu> list = new ArrayList<>();
        String sql = "SELECT ProdutoID, Quantidade FROM LinhaMenu WHERE MenuID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m.getId());
            ResultSet rs = ps.executeQuery();
            ProdutoDAOImpl pDao = new ProdutoDAOImpl();
            while (rs.next()) list.add(new LinhaMenu(pDao.findById(rs.getInt(1)), rs.getInt(2)));
        }
        m.setLinhasMenu(list);
    }

    @Override public List<Menu> findAll() {
        List<Menu> res = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection(); Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT ID FROM Menu")) {
            while (rs.next()) res.add(findById(rs.getInt(1)));
        } catch (SQLException e) { e.printStackTrace(); }
        return res;
    }

    @Override public boolean delete(Integer id) { identityMap.remove(id); return false; }
}
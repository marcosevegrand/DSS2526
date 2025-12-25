package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.MenuDAO;
import dss2526.domain.entity.LinhaMenu;
import dss2526.domain.entity.Menu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDAOImpl implements MenuDAO {
    private static MenuDAOImpl instance;
    private final DBConfig dbConfig = DBConfig.getInstance();

    public static MenuDAOImpl getInstance() {
        if(instance == null) instance = new MenuDAOImpl();
        return instance;
    }

    private MenuDAOImpl() {}

    @Override
    public Menu create(Menu m) {
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            String sql = "INSERT INTO Menu (Nome, Preco) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, m.getNome());
                stmt.setDouble(2, m.getPreco());
                stmt.executeUpdate();
                try(ResultSet rs = stmt.getGeneratedKeys()){
                    if(rs.next()) m.setId(rs.getInt(1));
                }
            }

            if (m.getLinhasMenu() != null) {
                try (PreparedStatement stmtL = conn.prepareStatement("INSERT INTO LinhaMenu (MenuId, ProdutoId, Quantidade) VALUES (?, ?, ?)")) {
                    for (LinhaMenu lm : m.getLinhasMenu()) {
                        stmtL.setInt(1, m.getId());
                        stmtL.setInt(2, lm.getIdProduto());
                        stmtL.setInt(3, lm.getQuantidade());
                        stmtL.addBatch();
                    }
                    stmtL.executeBatch();
                }
            }

            conn.commit();
            return m;
        } catch (SQLException e) {
            if(conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return null;
        } finally {
            if(conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public Menu findById(Integer id) {
        Menu m = null;
        try (Connection conn = dbConfig.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Menu WHERE Id=?")) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if(rs.next()) {
                        m = new Menu();
                        m.setId(rs.getInt("Id"));
                        m.setNome(rs.getString("Nome"));
                        m.setPreco(rs.getDouble("Preco"));
                    }
                }
            }

            if (m != null) {
                try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM LinhaMenu WHERE MenuId=?")) {
                    stmt.setInt(1, m.getId());
                    List<LinhaMenu> linhas = new ArrayList<>();
                    try (ResultSet rs = stmt.executeQuery()) {
                        while(rs.next()) {
                            LinhaMenu lm = new LinhaMenu();
                            lm.setId(rs.getInt("Id"));
                            lm.setIdProduto(rs.getInt("ProdutoId"));
                            lm.setQuantidade(rs.getInt("Quantidade"));
                            linhas.add(lm);
                        }
                    }
                    m.setLinhasMenu(linhas);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return m;
    }

    @Override
    public Menu update(Menu m) {
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement("UPDATE Menu SET Nome=?, Preco=? WHERE Id=?")) {
                stmt.setString(1, m.getNome());
                stmt.setDouble(2, m.getPreco());
                stmt.setInt(3, m.getId());
                stmt.executeUpdate();
            }

            // Update composition
            try (PreparedStatement stmtDel = conn.prepareStatement("DELETE FROM LinhaMenu WHERE MenuId=?")) {
                stmtDel.setInt(1, m.getId());
                stmtDel.executeUpdate();
            }

            if (m.getLinhasMenu() != null && !m.getLinhasMenu().isEmpty()) {
                try (PreparedStatement stmtL = conn.prepareStatement("INSERT INTO LinhaMenu (MenuId, ProdutoId, Quantidade) VALUES (?, ?, ?)")) {
                    for (LinhaMenu lm : m.getLinhasMenu()) {
                        stmtL.setInt(1, m.getId());
                        stmtL.setInt(2, lm.getIdProduto());
                        stmtL.setInt(3, lm.getQuantidade());
                        stmtL.addBatch();
                    }
                    stmtL.executeBatch();
                }
            }

            conn.commit();
            return m;
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

            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM LinhaMenu WHERE MenuId=?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            
            int rows;
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Menu WHERE Id=?")) {
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
    public List<Menu> findAll() {
        List<Menu> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT Id FROM Menu");
             ResultSet rs = stmt.executeQuery()) {
            while(rs.next()) {
                list.add(findById(rs.getInt("Id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
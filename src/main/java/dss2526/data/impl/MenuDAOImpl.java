package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.MenuDAO;
import dss2526.domain.entity.LinhaMenu;
import dss2526.domain.entity.Menu;

import java.sql.*;
import java.util.*;

public class MenuDAOImpl implements MenuDAO {
    private static MenuDAOImpl instance;
    private DBConfig dbConfig;
    
    // Identity Map for Menu (Entity)
    private Map<Integer, Menu> menuMap = new HashMap<>();

    // Identity Map for LinhaMenu (Composition)
    private Map<Integer, LinhaMenu> linhaMenuMap = new HashMap<>();

    private MenuDAOImpl() {
        this.dbConfig = DBConfig.getInstance();
    }

    public static synchronized MenuDAOImpl getInstance() {
        if (instance == null) instance = new MenuDAOImpl();
        return instance;
    }

    @Override
    public Menu create(Menu entity) {
        String sql = "INSERT INTO Menu (nome, preco) VALUES (?, ?)";
        String sqlLinha = "INSERT INTO LinhaMenu (menu_id, produto_id, quantidade) VALUES (?, ?, ?)";

        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, entity.getNome());
                ps.setDouble(2, entity.getPreco());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setId(rs.getInt(1));
                        menuMap.put(entity.getId(), entity);
                    }
                }

                try (PreparedStatement psL = conn.prepareStatement(sqlLinha, Statement.RETURN_GENERATED_KEYS)) {
                    for (LinhaMenu lm : entity.getLinhas()) {
                        psL.setInt(1, entity.getId());
                        psL.setInt(2, lm.getProdutoId());
                        psL.setInt(3, lm.getQuantidade());
                        psL.executeUpdate();
                        try (ResultSet rs = psL.getGeneratedKeys()) {
                            if (rs.next()) {
                                lm.setId(rs.getInt(1));
                                lm.setMenuId(entity.getId());
                                linhaMenuMap.put(lm.getId(), lm);
                            }
                        }
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Menu update(Menu entity) {
        String sql = "UPDATE Menu SET nome=?, preco=? WHERE id=?";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, entity.getNome());
                ps.setDouble(2, entity.getPreco());
                ps.setInt(3, entity.getId());
                ps.executeUpdate();

                // Simple update strategy: Delete lines and re-insert
                try (PreparedStatement psDel = conn.prepareStatement("DELETE FROM LinhaMenu WHERE menu_id=?")) {
                    psDel.setInt(1, entity.getId());
                    psDel.executeUpdate();
                }

                // Clear lines from Identity Map for this menu context 
                String sqlLinha = "INSERT INTO LinhaMenu (menu_id, produto_id, quantidade) VALUES (?, ?, ?)";
                try (PreparedStatement psL = conn.prepareStatement(sqlLinha, Statement.RETURN_GENERATED_KEYS)) {
                    for (LinhaMenu lm : entity.getLinhas()) {
                        psL.setInt(1, entity.getId());
                        psL.setInt(2, lm.getProdutoId());
                        psL.setInt(3, lm.getQuantidade());
                        psL.executeUpdate();
                        try (ResultSet rs = psL.getGeneratedKeys()) {
                            if (rs.next()) {
                                lm.setId(rs.getInt(1));
                                lm.setMenuId(entity.getId());
                                linhaMenuMap.put(lm.getId(), lm);
                            }
                        }
                    }
                }
                conn.commit();
                
                menuMap.put(entity.getId(), entity);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Menu findById(Integer id) {
        if (menuMap.containsKey(id)) {
            return menuMap.get(id);
        }

        String sql = "SELECT * FROM Menu WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Menu m = new Menu();
                    m.setId(rs.getInt("id"));
                    m.setNome(rs.getString("nome"));
                    m.setPreco(rs.getDouble("preco"));
                    
                    // Add to map before loading lines to prevent recursion issues if any
                    menuMap.put(m.getId(), m);
                    
                    m.setLinhas(findLinhas(conn, m.getId()));
                    return m;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<LinhaMenu> findLinhas(Connection conn, int menuId) throws SQLException {
        List<LinhaMenu> list = new ArrayList<>();
        String sql = "SELECT * FROM LinhaMenu WHERE menu_id = ? ORDER BY id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, menuId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    if (linhaMenuMap.containsKey(id)) {
                        list.add(linhaMenuMap.get(id));
                    } else {
                        LinhaMenu lm = new LinhaMenu();
                        lm.setId(id);
                        lm.setMenuId(rs.getInt("menu_id"));
                        lm.setProdutoId(rs.getInt("produto_id"));
                        lm.setQuantidade(rs.getInt("quantidade"));
                        linhaMenuMap.put(id, lm);
                        list.add(lm);
                    }
                }
            }
        }
        return list;
    }

    @Override
    public List<Menu> findAll() {
        List<Menu> list = new ArrayList<>();
        String sql = "SELECT id FROM Menu ORDER BY id";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(findById(rs.getInt("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Menu WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) menuMap.remove(id);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
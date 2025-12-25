package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.MenuDAO;
import dss2526.domain.entity.*;

import java.sql.*;
import java.util.*;

public class MenuDAOImpl implements MenuDAO {

    private DBConfig dbConfig = DBConfig.getInstance();
    private static Map<Integer, Menu> identityMap = new HashMap<>();

    // --- Métodos de Compatibilidade (Map Style) ---
    @Override public Menu get(int id) { return findById(id); }
    @Override public Collection<Menu> values() { return findAll(); }

    @Override
    public Menu save(Menu m) {
        String sql = "INSERT INTO Menu (Nome, Preco, Disponivel) VALUES (?, ?, ?)";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false); // Transação iniciada
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, m.getNome());
                ps.setDouble(2, m.getPreco());
                ps.setBoolean(3, m.isDisponivel());
                ps.executeUpdate();
                
                // Recuperar ID gerado para as linhas
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) m.setId(rs.getInt(1));
                }
                
                saveLines(conn, m);
                conn.commit();
                identityMap.put(m.getId(), m);
            } catch (SQLException e) { 
                conn.rollback(); 
                throw e; 
            }
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

                // Sincronização de linhas: Remove e reinsere
                try (PreparedStatement del = conn.prepareStatement("DELETE FROM LinhaMenu WHERE MenuID = ?")) {
                    del.setInt(1, m.getId()); 
                    del.executeUpdate();
                }
                
                saveLines(conn, m);
                conn.commit();
                identityMap.put(m.getId(), m);
            } catch (SQLException e) { 
                conn.rollback(); 
                throw e; 
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return m;
    }

    private void saveLines(Connection conn, Menu m) throws SQLException {
        if (m.getLinhasMenu() == null || m.getLinhasMenu().isEmpty()) return;
        
        String sql = "INSERT INTO LinhaMenu (MenuID, ProdutoID, Quantidade) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (LinhaMenu lm : m.getLinhasMenu()) {
                if (lm.getProduto() != null) {
                    ps.setInt(1, m.getId());
                    ps.setInt(2, lm.getProduto().getId());
                    ps.setInt(3, lm.getQuantidade());
                    ps.addBatch();
                }
            }
            ps.executeBatch();
        }
    }

    @Override
    public Menu findById(Integer id) {
        if (id == null || id <= 0) return null;
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
                    
                    identityMap.put(m.getId(), m); // Registrar antes de carregar filhos
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
            try (ResultSet rs = ps.executeQuery()) {
                ProdutoDAOImpl pDao = new ProdutoDAOImpl();
                while (rs.next()) {
                    Produto p = pDao.findById(rs.getInt("ProdutoID"));
                    if (p != null) {
                        list.add(new LinhaMenu(p, rs.getInt("Quantidade")));
                    }
                }
            }
        }
        m.setLinhasMenu(list);
    }

    @Override
    public List<Menu> findAll() {
        List<Menu> res = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection(); 
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT ID FROM Menu")) {
            while (rs.next()) {
                res.add(findById(rs.getInt(1)));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return res;
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null || id <= 0) return false;
        
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Apagar dependentes (Linhas)
                try (PreparedStatement psLinhas = conn.prepareStatement("DELETE FROM LinhaMenu WHERE MenuID = ?")) {
                    psLinhas.setInt(1, id);
                    psLinhas.executeUpdate();
                }
                // 2. Apagar o Menu
                try (PreparedStatement psMenu = conn.prepareStatement("DELETE FROM Menu WHERE ID = ?")) {
                    psMenu.setInt(1, id);
                    int rows = psMenu.executeUpdate();
                    
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
}
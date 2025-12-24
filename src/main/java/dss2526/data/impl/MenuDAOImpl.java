package dss2526.data.impl;

import dss2526.domain.entity.Menu;
import dss2526.domain.entity.LinhaMenu;
import dss2526.domain.entity.Produto;
import dss2526.data.config.DBConfig;
import dss2526.data.contract.MenuDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDAOImpl implements MenuDAO {

    @Override
    public void put(Integer key, Menu value) {
        try (Connection conn = DBConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Check if exists
                boolean exists = false;
                try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM menus WHERE id=?")) {
                    ps.setInt(1, key);
                    try (ResultSet rs = ps.executeQuery()) {
                        exists = rs.next();
                    }
                }

                if (exists) {
                    try (PreparedStatement ps = conn
                            .prepareStatement("UPDATE menus SET nome=?, preco=?, disponivel=? WHERE id=?")) {
                        ps.setString(1, value.getNome());
                        ps.setBigDecimal(2, value.getPreco());
                        ps.setBoolean(3, value.isDisponivel());
                        ps.setInt(4, key);
                        ps.executeUpdate();
                    }
                    // Clear lines
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM linha_menu WHERE menu_id=?")) {
                        ps.setInt(1, key);
                        ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = conn
                            .prepareStatement("INSERT INTO menus (id, nome, preco, disponivel) VALUES (?, ?, ?, ?)")) {
                        ps.setInt(1, key);
                        ps.setString(2, value.getNome());
                        ps.setBigDecimal(3, value.getPreco());
                        ps.setBoolean(4, value.isDisponivel());
                        ps.executeUpdate();
                    }
                }

                if (value.getLinhasMenu() != null) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO linha_menu (menu_id, produto_id, quantidade) VALUES (?, ?, ?)")) {
                        for (LinhaMenu lm : value.getLinhasMenu()) {
                            ps.setInt(1, key);
                            ps.setInt(2, lm.getProduto().getId());
                            ps.setInt(3, lm.getQuantidade());
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao guardar menu", e);
        }
    }

    @Override
    public Menu save(Menu value) {
        if (value.getId() != null && value.getId() != 0) {
            put(value.getId(), value);
            return value;
        }
        try (Connection conn = DBConfig.getConnection()) {
            int newId = 1;
            try (Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery("SELECT MAX(id) FROM menus")) {
                if (rs.next()) {
                    newId = rs.getInt(1) + 1;
                }
            }
            value.setId(newId);
            put(newId, value);
            return value;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao gerar ID para menu", e);
        }
    }

    @Override
    public Menu get(Integer key) {
        Menu m = null;
        try (Connection conn = DBConfig.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM menus WHERE id=?")) {
                ps.setInt(1, key);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        m = new Menu();
                        m.setId(rs.getInt("id"));
                        m.setNome(rs.getString("nome"));
                        m.setPreco(rs.getBigDecimal("preco"));
                        m.setDisponivel(rs.getBoolean("disponivel"));
                    }
                }
            }
            if (m != null) {
                loadLines(conn, m);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter menu", e);
        }
        return m;
    }

    private void loadLines(Connection conn, Menu m) throws SQLException {
        // Need to load Products. For simplicity, we only load ID+Name or basic info of
        // product.
        // Ideally we would use ProdutoDAO inside here, but that creates coupling.
        // We will do a basic fetch or dependency injection.
        // For JDBC simple implementation, let's just fetch basic product info required.
        String sql = "SELECT lm.quantidade, p.id, p.nome, p.preco, p.disponivel FROM linha_menu lm " +
                "JOIN produtos p ON lm.produto_id = p.id " +
                "WHERE lm.menu_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m.getId());
            try (ResultSet rs = ps.executeQuery()) {
                List<LinhaMenu> lines = new ArrayList<>();
                while (rs.next()) {
                    Produto p = new Produto();
                    p.setId(rs.getInt("id"));
                    p.setNome(rs.getString("nome"));
                    p.setPreco(rs.getBigDecimal("preco"));
                    p.setDisponivel(rs.getBoolean("disponivel"));

                    LinhaMenu lm = new LinhaMenu(p, rs.getInt("quantidade"));
                    // lm.setId(...) if needed?
                    lines.add(lm);
                }
                m.setLinhasMenu(lines);
            }
        }
    }

    @Override
    public Menu remove(Integer key) {
        Menu m = get(key);
        if (m != null) {
            try (Connection conn = DBConfig.getConnection()) {
                conn.setAutoCommit(false);
                try {
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM linha_menu WHERE menu_id=?")) {
                        ps.setInt(1, key);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM menus WHERE id=?")) {
                        ps.setInt(1, key);
                        ps.executeUpdate();
                    }
                    conn.commit();
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao remover menu", e);
            }
        }
        return m;
    }

    @Override
    public boolean containsKey(Integer key) {
        try (Connection conn = DBConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM menus WHERE id=?")) {
            ps.setInt(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Menu> values() {
        List<Menu> list = new ArrayList<>();
        try (Connection conn = DBConfig.getConnection()) {
            try (Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery("SELECT * FROM menus")) {
                while (rs.next()) {
                    Menu m = new Menu();
                    m.setId(rs.getInt("id"));
                    m.setNome(rs.getString("nome"));
                    m.setPreco(rs.getBigDecimal("preco"));
                    m.setDisponivel(rs.getBoolean("disponivel"));
                    list.add(m);
                }
            }
            for (Menu m : list) {
                loadLines(conn, m);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar menus", e);
        }
        return list;
    }

    @Override
    public int size() {
        try (Connection conn = DBConfig.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT count(*) FROM menus")) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
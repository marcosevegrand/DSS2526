package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.CatalogoDAO;
import dss2526.domain.entity.Catalogo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatalogoDAOImpl implements CatalogoDAO {

    private static CatalogoDAOImpl instance;
    private final DBConfig dbConfig = DBConfig.getInstance();
    
    public static CatalogoDAOImpl getInstance() {
        if(instance == null) instance = new CatalogoDAOImpl();
        return instance;
    }
    
    private CatalogoDAOImpl() {}

    @Override
    public Catalogo create(Catalogo obj) {
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            // 1. Inserir Catalogo
            String sql = "INSERT INTO Catalogo (Nome) VALUES (?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, obj.getNome());
                stmt.executeUpdate();
                
                try(ResultSet rs = stmt.getGeneratedKeys()){
                    if(rs.next()) {
                        obj.setId(rs.getInt(1));
                    }
                }
            }

            // 2. Inserir IDs dos Menus associados
            if (obj.getMenuIds() != null && !obj.getMenuIds().isEmpty()) {
                String sqlMenu = "INSERT INTO Catalogo_Menu (CatalogoId, MenuId) VALUES (?, ?)";
                try (PreparedStatement stmtMenu = conn.prepareStatement(sqlMenu)) {
                    for (Integer menuId : obj.getMenuIds()) {
                        stmtMenu.setInt(1, obj.getId());
                        stmtMenu.setInt(2, menuId);
                        stmtMenu.addBatch();
                    }
                    stmtMenu.executeBatch();
                }
            }

            // 3. Inserir IDs dos Produtos associados
            if (obj.getProdutoIds() != null && !obj.getProdutoIds().isEmpty()) {
                String sqlProd = "INSERT INTO Catalogo_Produto (CatalogoId, ProdutoId) VALUES (?, ?)";
                try (PreparedStatement stmtProd = conn.prepareStatement(sqlProd)) {
                    for (Integer produtoId : obj.getProdutoIds()) {
                        stmtProd.setInt(1, obj.getId());
                        stmtProd.setInt(2, produtoId);
                        stmtProd.addBatch();
                    }
                    stmtProd.executeBatch();
                }
            }

            conn.commit();
            return obj;
        } catch (SQLException e) {
            if(conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return null;
        } finally {
            if(conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public Catalogo findById(Integer id) {
        Catalogo c = null;
        try (Connection conn = dbConfig.getConnection()) {
            // Ler Catalogo
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Catalogo WHERE Id=?")) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        c = new Catalogo();
                        c.setId(rs.getInt("Id"));
                        c.setNome(rs.getString("Nome"));
                    }
                }
            }

            if (c != null) {
                // Carregar IDs dos Menus (apenas inteiros)
                String sqlMenus = "SELECT MenuId FROM Catalogo_Menu WHERE CatalogoId=?";
                List<Integer> menuIds = new ArrayList<>();
                try (PreparedStatement stmt = conn.prepareStatement(sqlMenus)) {
                    stmt.setInt(1, c.getId());
                    try (ResultSet rs = stmt.executeQuery()) {
                        while(rs.next()) {
                            menuIds.add(rs.getInt("MenuId"));
                        }
                    }
                }
                c.setMenuIds(menuIds);

                // Carregar IDs dos Produtos (apenas inteiros)
                String sqlProds = "SELECT ProdutoId FROM Catalogo_Produto WHERE CatalogoId=?";
                List<Integer> produtoIds = new ArrayList<>();
                try (PreparedStatement stmt = conn.prepareStatement(sqlProds)) {
                    stmt.setInt(1, c.getId());
                    try (ResultSet rs = stmt.executeQuery()) {
                        while(rs.next()) {
                            produtoIds.add(rs.getInt("ProdutoId"));
                        }
                    }
                }
                c.setProdutoIds(produtoIds);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    @Override
    public Catalogo update(Catalogo obj) {
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            // 1. Atualizar Catalogo
            try (PreparedStatement stmt = conn.prepareStatement("UPDATE Catalogo SET Nome=? WHERE Id=?")) {
                stmt.setString(1, obj.getNome());
                stmt.setInt(2, obj.getId());
                stmt.executeUpdate();
            }

            // 2. Atualizar Menus (Delete All + Insert)
            try (PreparedStatement stmtDel = conn.prepareStatement("DELETE FROM Catalogo_Menu WHERE CatalogoId=?")) {
                stmtDel.setInt(1, obj.getId());
                stmtDel.executeUpdate();
            }
            if (obj.getMenuIds() != null && !obj.getMenuIds().isEmpty()) {
                String sqlMenu = "INSERT INTO Catalogo_Menu (CatalogoId, MenuId) VALUES (?, ?)";
                try (PreparedStatement stmtMenu = conn.prepareStatement(sqlMenu)) {
                    for (Integer menuId : obj.getMenuIds()) {
                        stmtMenu.setInt(1, obj.getId());
                        stmtMenu.setInt(2, menuId);
                        stmtMenu.addBatch();
                    }
                    stmtMenu.executeBatch();
                }
            }

            // 3. Atualizar Produtos (Delete All + Insert)
            try (PreparedStatement stmtDel = conn.prepareStatement("DELETE FROM Catalogo_Produto WHERE CatalogoId=?")) {
                stmtDel.setInt(1, obj.getId());
                stmtDel.executeUpdate();
            }
            if (obj.getProdutoIds() != null && !obj.getProdutoIds().isEmpty()) {
                String sqlProd = "INSERT INTO Catalogo_Produto (CatalogoId, ProdutoId) VALUES (?, ?)";
                try (PreparedStatement stmtProd = conn.prepareStatement(sqlProd)) {
                    for (Integer produtoId : obj.getProdutoIds()) {
                        stmtProd.setInt(1, obj.getId());
                        stmtProd.setInt(2, produtoId);
                        stmtProd.addBatch();
                    }
                    stmtProd.executeBatch();
                }
            }

            conn.commit();
            return obj;
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

            // Remover Associações
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Catalogo_Menu WHERE CatalogoId=?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Catalogo_Produto WHERE CatalogoId=?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

            // Remover Catalogo
            int rows;
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Catalogo WHERE Id=?")) {
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
    public List<Catalogo> findAll() {
        List<Catalogo> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT Id FROM Catalogo");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(findById(rs.getInt("Id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
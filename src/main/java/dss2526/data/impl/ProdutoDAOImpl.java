package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.ProdutoDAO;
import dss2526.domain.entity.LinhaProduto;
import dss2526.domain.entity.Passo;
import dss2526.domain.entity.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAOImpl implements ProdutoDAO {
    private static ProdutoDAOImpl instance;
    private final DBConfig dbConfig = DBConfig.getInstance();

    public static ProdutoDAOImpl getInstance() {
        if(instance == null) instance = new ProdutoDAOImpl();
        return instance;
    }

    private ProdutoDAOImpl() {}

    @Override
    public Produto create(Produto p) {
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert Produto
            String sqlProd = "INSERT INTO Produto (Nome, Preco) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlProd, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, p.getNome());
                stmt.setDouble(2, p.getPreco());
                stmt.executeUpdate();
                
                try(ResultSet rs = stmt.getGeneratedKeys()){
                    if(rs.next()) p.setId(rs.getInt(1));
                }
            }

            // 2. Insert Ingredientes (Composition)
            if (p.getIngredientes() != null) {
                String sqlLinha = "INSERT INTO LinhaProduto (ProdutoId, IngredienteId, Quantidade) VALUES (?, ?, ?)";
                try (PreparedStatement stmtLinha = conn.prepareStatement(sqlLinha)) {
                    for (LinhaProduto linha : p.getIngredientes()) {
                        stmtLinha.setInt(1, p.getId());
                        stmtLinha.setInt(2, linha.getIdIngrediente());
                        stmtLinha.setDouble(3, linha.getQuantidade());
                        stmtLinha.addBatch();
                    }
                    stmtLinha.executeBatch();
                }
            }

            // 3. Insert Tarefas/Passos (Association/Link Table)
            // Assuming table Produto_Passo(ProdutoId, PassoId)
            if (p.getTarefas() != null) {
                String sqlPasso = "INSERT INTO Produto_Passo (ProdutoId, PassoId) VALUES (?, ?)";
                try (PreparedStatement stmtPasso = conn.prepareStatement(sqlPasso)) {
                    for (Passo passo : p.getTarefas()) {
                        stmtPasso.setInt(1, p.getId());
                        stmtPasso.setInt(2, passo.getId());
                        stmtPasso.addBatch();
                    }
                    stmtPasso.executeBatch();
                }
            }
            
            conn.commit();
            return p;
        } catch (SQLException e) {
            if(conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return null;
        } finally {
            if(conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public Produto findById(Integer id) {
        Produto p = null;
        try (Connection conn = dbConfig.getConnection()) {
            // Read Produto
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Produto WHERE Id=?")) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        p = new Produto();
                        p.setId(rs.getInt("Id"));
                        p.setNome(rs.getString("Nome"));
                        p.setPreco(rs.getDouble("Preco"));
                    }
                }
            }
            
            if (p != null) {
                // Read Ingredients
                String sqlLinhas = "SELECT * FROM LinhaProduto WHERE ProdutoId=?";
                List<LinhaProduto> ingredientes = new ArrayList<>();
                try (PreparedStatement stmt = conn.prepareStatement(sqlLinhas)) {
                    stmt.setInt(1, p.getId());
                    try (ResultSet rs = stmt.executeQuery()) {
                        while(rs.next()) {
                            LinhaProduto lp = new LinhaProduto();
                            lp.setId(rs.getInt("Id"));
                            lp.setIdIngrediente(rs.getInt("IngredienteId"));
                            lp.setQuantidade(rs.getDouble("Quantidade"));
                            ingredientes.add(lp);
                        }
                    }
                }
                p.setIngredientes(ingredientes);

                // Read Tarefas (Passos)
                String sqlPassos = "SELECT P.* FROM Passo P JOIN Produto_Passo PP ON P.Id = PP.PassoId WHERE PP.ProdutoId = ?";
                List<Passo> tarefas = new ArrayList<>();
                try(PreparedStatement stmt = conn.prepareStatement(sqlPassos)){
                    stmt.setInt(1, p.getId());
                    try(ResultSet rs = stmt.executeQuery()){
                        while(rs.next()){
                            Passo passo = PassoDAOImpl.getInstance().findById(rs.getInt("Id"));
                            if(passo != null) tarefas.add(passo);
                        }
                    }
                }
                p.setTarefas(tarefas);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }

    @Override
    public Produto update(Produto p) {
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);

            // 1. Update Produto
            try (PreparedStatement stmt = conn.prepareStatement("UPDATE Produto SET Nome=?, Preco=? WHERE Id=?")) {
                stmt.setString(1, p.getNome());
                stmt.setDouble(2, p.getPreco());
                stmt.setInt(3, p.getId());
                stmt.executeUpdate();
            }

            // 2. Update Ingredients (Delete + Insert)
            try (PreparedStatement stmtDel = conn.prepareStatement("DELETE FROM LinhaProduto WHERE ProdutoId=?")) {
                stmtDel.setInt(1, p.getId());
                stmtDel.executeUpdate();
            }
            if (p.getIngredientes() != null && !p.getIngredientes().isEmpty()) {
                String sqlLinha = "INSERT INTO LinhaProduto (ProdutoId, IngredienteId, Quantidade) VALUES (?, ?, ?)";
                try (PreparedStatement stmtLinha = conn.prepareStatement(sqlLinha)) {
                    for (LinhaProduto linha : p.getIngredientes()) {
                        stmtLinha.setInt(1, p.getId());
                        stmtLinha.setInt(2, linha.getIdIngrediente());
                        stmtLinha.setDouble(3, linha.getQuantidade());
                        stmtLinha.addBatch();
                    }
                    stmtLinha.executeBatch();
                }
            }

            // 3. Update Tarefas/Passos (Delete + Insert)
            try (PreparedStatement stmtDel = conn.prepareStatement("DELETE FROM Produto_Passo WHERE ProdutoId=?")) {
                stmtDel.setInt(1, p.getId());
                stmtDel.executeUpdate();
            }
            if (p.getTarefas() != null && !p.getTarefas().isEmpty()) {
                String sqlPasso = "INSERT INTO Produto_Passo (ProdutoId, PassoId) VALUES (?, ?)";
                try (PreparedStatement stmtPasso = conn.prepareStatement(sqlPasso)) {
                    for (Passo passo : p.getTarefas()) {
                        stmtPasso.setInt(1, p.getId());
                        stmtPasso.setInt(2, passo.getId());
                        stmtPasso.addBatch();
                    }
                    stmtPasso.executeBatch();
                }
            }

            conn.commit();
            return p;
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

            // Delete links
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM LinhaProduto WHERE ProdutoId=?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Produto_Passo WHERE ProdutoId=?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            
            int rows;
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Produto WHERE Id=?")) {
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
    public List<Produto> findAll() {
        List<Produto> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT Id FROM Produto");
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
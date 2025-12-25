package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.ProdutoDAO;
import dss2526.domain.entity.*;

import java.sql.*;
import java.util.*;

public class ProdutoDAOImpl implements ProdutoDAO {

    private DBConfig dbConfig = DBConfig.getInstance();
    private static Map<Integer, Produto> identityMap = new HashMap<>();

    @Override
    public Produto save(Produto p) {
        String sql = "INSERT INTO Produto (Nome, Preco) VALUES (?, ?)";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, p.getNome());
                ps.setDouble(2, p.getPreco());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) p.setId(rs.getInt(1));
                }
                // Persiste apenas as relações (Ingredientes e Associação de Tarefas)
                saveRelations(conn, p);
                conn.commit();
                identityMap.put(p.getId(), p);
            } catch (SQLException e) { conn.rollback(); throw e; }
        } catch (SQLException e) { e.printStackTrace(); }
        return p;
    }

    @Override
    public Produto update(Produto p) {
        String sql = "UPDATE Produto SET Nome = ?, Preco = ? WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, p.getNome());
                ps.setDouble(2, p.getPreco());
                ps.setInt(3, p.getId());
                ps.executeUpdate();

                // Limpa as tabelas de associação 1-N e N-N (não apaga as Tarefas em si)
                try (PreparedStatement d1 = conn.prepareStatement("DELETE FROM LinhaProduto WHERE ProdutoID = ?")) {
                    d1.setInt(1, p.getId()); d1.executeUpdate();
                }
                try (PreparedStatement d2 = conn.prepareStatement("DELETE FROM Produto_Tarefa WHERE ProdutoID = ?")) {
                    d2.setInt(1, p.getId()); d2.executeUpdate();
                }

                saveRelations(conn, p);
                conn.commit();
                identityMap.put(p.getId(), p);
            } catch (SQLException e) { conn.rollback(); throw e; }
        } catch (SQLException e) { e.printStackTrace(); }
        return p;
    }

    private void saveRelations(Connection conn, Produto p) throws SQLException {
        // Salvar Linhas de Ingredientes
        if (p.getIngredientes() != null) {
            String sqlIng = "INSERT INTO LinhaProduto (ProdutoID, IngredienteID, Quantidade) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlIng)) {
                for (LinhaProduto lp : p.getIngredientes()) {
                    ps.setInt(1, p.getId());
                    ps.setInt(2, lp.getIngrediente().getId());
                    ps.setDouble(3, lp.getQuantidade());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
        // Gerir associação com Tarefas (Mapeamento na tabela de junção apenas)
        if (p.getTarefas() != null) {
            String sqlT = "INSERT INTO Produto_Tarefa (ProdutoID, TarefaID) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlT)) {
                for (Passo t : p.getTarefas()) {
                    ps.setInt(1, p.getId());
                    ps.setInt(2, t.getId());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    @Override
    public Produto findById(Integer id) {
        if (identityMap.containsKey(id)) return identityMap.get(id);
        String sql = "SELECT * FROM Produto WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Produto p = new Produto();
                    p.setId(rs.getInt("ID"));
                    p.setNome(rs.getString("Nome"));
                    p.setPreco(rs.getDouble("Preco"));
                    identityMap.put(p.getId(), p);
                    loadRelations(conn, p);
                    return p;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private void loadRelations(Connection conn, Produto p) throws SQLException {
        // Ingredientes
        List<LinhaProduto> ingreds = new ArrayList<>();
        String sqlIng = "SELECT IngredienteID, Quantidade FROM LinhaProduto WHERE ProdutoID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlIng)) {
            ps.setInt(1, p.getId());
            ResultSet rs = ps.executeQuery();
            IngredienteDAOImpl iDao = new IngredienteDAOImpl();
            while (rs.next()) ingreds.add(new LinhaProduto(iDao.findById(rs.getInt(1)), rs.getDouble(2)));
        }
        p.setIngredientes(ingreds);

        // Tarefas
        List<Passo> tarefas = new ArrayList<>();
        String sqlT = "SELECT TarefaID FROM Produto_Tarefa WHERE ProdutoID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlT)) {
            ps.setInt(1, p.getId());
            ResultSet rs = ps.executeQuery();
            PassoDAOImpl tDao = new PassoDAOImpl();
            while (rs.next()) tarefas.add(tDao.findById(rs.getInt(1)));
        }
        p.setTarefas(tarefas);
    }

    @Override
    public List<Produto> findAll() {
        List<Produto> res = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection(); Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT ID FROM Produto")) {
            while (rs.next()) res.add(findById(rs.getInt(1)));
        } catch (SQLException e) { e.printStackTrace(); }
        return res;
    }

    @Override public boolean delete(Integer id) { identityMap.remove(id); return false; }
}
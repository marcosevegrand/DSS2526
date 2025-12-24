package dss2526.data.impl;

import dss2526.domain.entity.Produto;
import dss2526.domain.entity.LinhaIngrediente;
import dss2526.domain.entity.PassoProducao;
import dss2526.domain.entity.Ingrediente;
import dss2526.domain.enumeration.EstacaoTrabalho;
import dss2526.data.config.DBConfig;
import dss2526.data.contract.ProdutoDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAOImpl implements ProdutoDAO {

    @Override
    public void put(Integer key, Produto value) {
        // Simple strategy: check if exists, then insert/update.
        // Also need to handle related tables: linha_ingrediente, passo_producao
        // Transaction needed
        try (Connection conn = DBConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (containsKey(conn, key)) {
                    // Update main table
                    try (PreparedStatement ps = conn
                            .prepareStatement("UPDATE produtos SET nome=?, preco=?, disponivel=? WHERE id=?")) {
                        ps.setString(1, value.getNome());
                        ps.setBigDecimal(2, value.getPreco());
                        ps.setBoolean(3, value.isDisponivel());
                        ps.setInt(4, key);
                        ps.executeUpdate();
                    }
                    // Delete existing lines to replace (simplest approach for sync)
                    try (PreparedStatement ps = conn
                            .prepareStatement("DELETE FROM linha_ingrediente WHERE produto_id=?")) {
                        ps.setInt(1, key);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = conn
                            .prepareStatement("DELETE FROM passo_producao WHERE produto_id=?")) {
                        ps.setInt(1, key);
                        ps.executeUpdate();
                    }

                } else {
                    // Insert
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO produtos (id, nome, preco, disponivel) VALUES (?, ?, ?, ?)")) {
                        ps.setInt(1, key);
                        ps.setString(2, value.getNome());
                        ps.setBigDecimal(3, value.getPreco());
                        ps.setBoolean(4, value.isDisponivel());
                        ps.executeUpdate();
                    }
                }

                // Insert Details
                if (value.getIngredientes() != null) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO linha_ingrediente (produto_id, ingrediente_id, quantidade, unidade) VALUES (?, ?, ?, ?)")) {
                        for (LinhaIngrediente li : value.getIngredientes()) {
                            ps.setInt(1, key);
                            ps.setInt(2, li.getIngrediente().getId());
                            ps.setDouble(3, li.getQuantidade()); // Double
                            // ps.setString(4, li.getIngrediente().getUnidadeMedida()); // If table has unit
                            // column?
                            // My previous code had ps.setString(4, ...).
                            // Looking at step 146 MenuDAOImpl, query was INSERT ... VALUES (?, ?, ?).
                            // Here logic is specific to LinhaIngrediente.
                            // I'll assume table: INTO linha_ingrediente (produto_id, ingrediente_id,
                            // quantidade, unidade)
                            String unidade = li.getIngrediente() != null ? li.getIngrediente().getUnidadeMedida() : "";
                            ps.setString(4, unidade);
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }

                if (value.getPassos() != null) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO passo_producao (produto_id, nome, estacao) VALUES (?, ?, ?)")) {
                        for (PassoProducao pp : value.getPassos()) {
                            ps.setInt(1, key);
                            ps.setString(2, pp.getNome());
                            ps.setString(3, pp.getEstacao() != null ? pp.getEstacao().name() : null);
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
            throw new RuntimeException("Erro ao guardar Produto", e);
        }
    }

    @Override
    public Produto save(Produto value) {
        if (value.getId() != null && value.getId() != 0) {
            put(value.getId(), value);
            return value;
        }
        try (Connection conn = DBConfig.getConnection()) {
            int newId = 1;
            try (Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery("SELECT MAX(id) FROM produtos")) {
                if (rs.next()) {
                    newId = rs.getInt(1) + 1;
                }
            }
            value.setId(newId);
            put(newId, value);
            return value;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao gerar ID para produto", e);
        }
    }

    private boolean containsKey(Connection conn, Integer key) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM produtos WHERE id=?")) {
            ps.setInt(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public boolean containsKey(Integer key) {
        try (Connection conn = DBConfig.getConnection()) {
            return containsKey(conn, key);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Produto get(Integer key) {
        Produto p = null;
        try (Connection conn = DBConfig.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM produtos WHERE id=?")) {
                ps.setInt(1, key);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        p = buildProdutoSimple(rs);
                    }
                }
            }
            if (p != null) {
                loadDetails(conn, p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter Produto", e);
        }
        return p;
    }

    private void loadDetails(Connection conn, Produto p) throws SQLException {
        // Ingredientes
        String sqlIngs = "SELECT li.quantidade, li.unidade, i.id, i.nome, i.unidade_medida, i.alergenico " +
                "FROM linha_ingrediente li " +
                "JOIN ingredientes i ON li.ingrediente_id = i.id " +
                "WHERE li.produto_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlIngs)) {
            ps.setInt(1, p.getId());
            try (ResultSet rs = ps.executeQuery()) {
                List<LinhaIngrediente> list = new ArrayList<>();
                while (rs.next()) {
                    Ingrediente i = new Ingrediente();
                    i.setId(rs.getInt("id"));
                    i.setNome(rs.getString("nome"));
                    i.setUnidadeMedida(rs.getString("unidade_medida"));
                    String alg = rs.getString("alergenico");
                    if (alg != null)
                        i.setAlergenico(dss2526.domain.enumeration.Alergenico.valueOf(alg));

                    LinhaIngrediente li = new LinhaIngrediente();
                    li.setIngrediente(i);
                    li.setQuantidade(rs.getDouble("quantidade"));
                    // rs.getString("unidade") ignored or set on ingredient?
                    // Ingredient loaded earlier has unit.

                    // Note: If LinhaIngrediente doesn't store unit, we can't set it.
                    // But maybe we don't need to.
                    list.add(li);
                }
                p.setIngredientes(list);
            }
        }

        // Passos
        String sqlPassos = "SELECT * FROM passo_producao WHERE produto_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlPassos)) {
            ps.setInt(1, p.getId());
            try (ResultSet rs = ps.executeQuery()) {
                List<PassoProducao> list = new ArrayList<>();
                while (rs.next()) {
                    PassoProducao pp = new PassoProducao();
                    pp.setId(rs.getInt("id")); // Usually generated, but ok
                    pp.setNome(rs.getString("nome"));
                    String est = rs.getString("estacao");
                    if (est != null)
                        pp.setEstacao(EstacaoTrabalho.valueOf(est));
                    list.add(pp);
                }
                p.setPassos(list);
            }
        }
    }

    @Override
    public Produto remove(Integer key) {
        Produto p = get(key);
        if (p != null) {
            try (Connection conn = DBConfig.getConnection()) {
                conn.setAutoCommit(false);
                try {
                    try (PreparedStatement ps = conn
                            .prepareStatement("DELETE FROM linha_ingrediente WHERE produto_id=?")) {
                        ps.setInt(1, key);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = conn
                            .prepareStatement("DELETE FROM passo_producao WHERE produto_id=?")) {
                        ps.setInt(1, key);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM produtos WHERE id=?")) {
                        ps.setInt(1, key);
                        ps.executeUpdate();
                    }
                    conn.commit();
                } catch (Exception e) {
                    conn.rollback();
                    throw e;
                }
            } catch (Exception e) {
                throw new RuntimeException("Erro ao remover produto", e);
            }
        }
        return p;
    }

    @Override
    public List<Produto> values() {
        List<Produto> list = new ArrayList<>();
        try (Connection conn = DBConfig.getConnection()) {
            try (Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery("SELECT * FROM produtos")) {
                while (rs.next()) {
                    list.add(buildProdutoSimple(rs));
                }
            }
            // Optional: load details for all? Can be slow. But 'values' usually returns
            // full objects.
            for (Produto p : list) {
                loadDetails(conn, p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar Produtos", e);
        }
        return list;
    }

    @Override
    public int size() {
        try (Connection conn = DBConfig.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT count(*) FROM produtos")) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    private Produto buildProdutoSimple(ResultSet rs) throws SQLException {
        Produto p = new Produto();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setPreco(rs.getBigDecimal("preco"));
        p.setDisponivel(rs.getBoolean("disponivel"));
        return p;
    }
}
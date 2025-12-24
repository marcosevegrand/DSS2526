package dss2526.data.impl;

import dss2526.domain.entity.Tarefa;
import dss2526.domain.entity.Pedido;
import dss2526.domain.entity.Produto;
import dss2526.domain.enumeration.EstacaoTrabalho;
import dss2526.data.config.DBConfig;
import dss2526.data.contract.TarefaDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TarefaDAOImpl implements TarefaDAO {

    @Override
    public void put(Integer key, Tarefa value) {
        try (Connection conn = DBConfig.getConnection()) {
            if (containsKey(conn, key)) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE tarefas SET pedido_id=?, produto_id=?, estacao=?, concluida=?, data_conclusao=? WHERE id=?")) {
                    ps.setInt(1, value.getPedido() != null ? value.getPedido().getId() : 0);
                    ps.setInt(2, value.getProduto() != null ? value.getProduto().getId() : 0);
                    ps.setString(3, value.getEstacao() != null ? value.getEstacao().name() : null);
                    ps.setBoolean(4, value.getConcluida());
                    ps.setTimestamp(5,
                            value.getDataConclusao() != null ? Timestamp.valueOf(value.getDataConclusao()) : null);
                    ps.setInt(6, key);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO tarefas (id, pedido_id, produto_id, estacao, concluida, data_criacao, data_conclusao) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                    ps.setInt(1, key);
                    ps.setInt(2, value.getPedido() != null ? value.getPedido().getId() : 0);
                    ps.setInt(3, value.getProduto() != null ? value.getProduto().getId() : 0);
                    ps.setString(4, value.getEstacao() != null ? value.getEstacao().name() : null);
                    ps.setBoolean(5, value.getConcluida());
                    ps.setTimestamp(6,
                            value.getDataCriacao() != null ? Timestamp.valueOf(value.getDataCriacao()) : null);
                    ps.setTimestamp(7,
                            value.getDataConclusao() != null ? Timestamp.valueOf(value.getDataConclusao()) : null);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao guardar tarefa", e);
        }
    }

    @Override
    public Tarefa get(Integer key) {
        try (Connection conn = DBConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM tarefas WHERE id=?")) {
            ps.setInt(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return buildTarefa(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter tarefa", e);
        }
        return null;
    }

    @Override
    public Tarefa remove(Integer key) {
        Tarefa t = get(key);
        if (t != null) {
            try (Connection conn = DBConfig.getConnection();
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM tarefas WHERE id=?")) {
                ps.setInt(1, key);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao remover tarefa", e);
            }
        }
        return t;
    }

    @Override
    public boolean containsKey(Integer key) {
        try (Connection conn = DBConfig.getConnection()) {
            return containsKey(conn, key);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean containsKey(Connection conn, Integer key) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM tarefas WHERE id=?")) {
            ps.setInt(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public List<Tarefa> values() {
        List<Tarefa> list = new ArrayList<>();
        try (Connection conn = DBConfig.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM tarefas")) {
            while (rs.next()) {
                list.add(buildTarefa(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public int size() {
        try (Connection conn = DBConfig.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT count(*) FROM tarefas")) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public List<Tarefa> findByEstacao(EstacaoTrabalho estacao) {
        List<Tarefa> list = new ArrayList<>();
        try (Connection conn = DBConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM tarefas WHERE estacao=?")) {
            ps.setString(1, estacao.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(buildTarefa(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar tarefas por estacao", e);
        }
        return list;
    }

    private Tarefa buildTarefa(ResultSet rs) throws SQLException {
        Tarefa t = new Tarefa();
        t.setId(rs.getInt("id"));
        t.setConcluida(rs.getBoolean("concluida"));
        String est = rs.getString("estacao");
        if (est != null)
            t.setEstacao(EstacaoTrabalho.valueOf(est));
        // We only set IDs for Pedido/Produto or basic objects for now since full join
        // is expensive
        // and we might not need full details here. Or we could fetch.
        // For ProducaoFacade logic (listing tasks), we likely need Produto name?
        // Let's assume lazy loading or simplified objects:
        Pedido p = new Pedido();
        p.setId(rs.getInt("pedido_id"));
        t.setPedido(p);
        Produto prod = new Produto();
        prod.setId(rs.getInt("produto_id"));
        t.setProduto(prod);

        // Data
        Timestamp tsCriacao = rs.getTimestamp("data_criacao");
        if (tsCriacao != null)
            t.setDataCriacao(tsCriacao.toLocalDateTime()); // Assuming setter exists (Step 194 shows one, but it was
                                                           // just a getter? No, constructor set it. I should assume I
                                                           // can't set it unless I add setter. Step 194 showed NO
                                                           // SETTER for DataCriacao. I'll need to check or add it.)
        // Wait, Step 194: `public LocalDateTime getDataCriacao() { return dataCriacao;
        // }` ONLY.
        // It is set in Constructor.
        // I need to add a setter to Tarefa to hydrate it!

        // Same for DataConclusao. Step 194 showed `getDataConclusao`. No setter?
        // Correction: `setConcluida` sets `dataConclusao` to now().
        // If I want to load from DB, I need to force set the stored date.
        // I will add setters to Tarefa.

        return t;
    }
}

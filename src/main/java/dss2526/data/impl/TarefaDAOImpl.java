package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.TarefaDAO;
import dss2526.domain.entity.Tarefa;

import java.sql.*;
import java.util.*;

public class TarefaDAOImpl implements TarefaDAO {
    private static TarefaDAOImpl instance;
    private DBConfig dbConfig;

    // Identity Map for Tarefa
    private Map<Integer, Tarefa> tarefaMap = new HashMap<>();

    private TarefaDAOImpl() {
        this.dbConfig = DBConfig.getInstance();
    }

    public static synchronized TarefaDAOImpl getInstance() {
        if (instance == null) instance = new TarefaDAOImpl();
        return instance;
    }

    @Override
    public Tarefa create(Tarefa entity) {
        String sql = "INSERT INTO Tarefa (passo_id, produto_id, pedido_id, data_criacao, data_conclusao, concluido) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entity.getPassoId());
            ps.setInt(2, entity.getProdutoId());
            ps.setInt(3, entity.getPedidoId());
            ps.setTimestamp(4, Timestamp.valueOf(entity.getDataCriacao()));
            if (entity.getDataConclusao() != null)
                ps.setTimestamp(5, Timestamp.valueOf(entity.getDataConclusao()));
            else
                ps.setNull(5, Types.TIMESTAMP);
            ps.setBoolean(6, entity.isConcluido());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getInt(1));
                    tarefaMap.put(entity.getId(), entity);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Tarefa update(Tarefa entity) {
        String sql = "UPDATE Tarefa SET passo_id=?, produto_id=?, pedido_id=?, data_criacao=?, data_conclusao=?, concluido=? WHERE id=?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entity.getPassoId());
            ps.setInt(2, entity.getProdutoId());
            ps.setInt(3, entity.getPedidoId());
            ps.setTimestamp(4, Timestamp.valueOf(entity.getDataCriacao()));
            if (entity.getDataConclusao() != null)
                ps.setTimestamp(5, Timestamp.valueOf(entity.getDataConclusao()));
            else
                ps.setNull(5, Types.TIMESTAMP);
            ps.setBoolean(6, entity.isConcluido());
            ps.setInt(7, entity.getId());
            ps.executeUpdate();
            tarefaMap.put(entity.getId(), entity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Tarefa findById(Integer id) {
        if (tarefaMap.containsKey(id)) {
            return tarefaMap.get(id);
        }

        String sql = "SELECT * FROM Tarefa WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Tarefa t = map(rs);
                    tarefaMap.put(t.getId(), t);
                    return t;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Tarefa map(ResultSet rs) throws SQLException {
        Tarefa t = new Tarefa();
        t.setId(rs.getInt("id"));
        t.setPassoId(rs.getInt("passo_id"));
        t.setProdutoId(rs.getInt("produto_id"));
        t.setPedidoId(rs.getInt("pedido_id"));
        t.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        if (rs.getTimestamp("data_conclusao") != null)
            t.setDataConclusao(rs.getTimestamp("data_conclusao").toLocalDateTime());
        t.setConcluido(rs.getBoolean("concluido"));
        return t;
    }

    @Override
    public List<Tarefa> findAll() {
        List<Tarefa> list = new ArrayList<>();
        String sql = "SELECT * FROM Tarefa ORDER BY id";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                if (tarefaMap.containsKey(id)) {
                    list.add(tarefaMap.get(id));
                } else {
                    Tarefa t = map(rs);
                    tarefaMap.put(t.getId(), t);
                    list.add(t);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Tarefa> findAllByPedido(int pedidoId) {
        List<Tarefa> list = new ArrayList<>();
        String sql = "SELECT * FROM Tarefa WHERE pedido_id = ? ORDER BY id";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pedidoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    if (tarefaMap.containsKey(id)) {
                        list.add(tarefaMap.get(id));
                    } else {
                        Tarefa t = map(rs);
                        tarefaMap.put(t.getId(), t);
                        list.add(t);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Tarefa> findAllByPasso(int passoId) {
        List<Tarefa> list = new ArrayList<>();
        String sql = "SELECT * FROM Tarefa WHERE passo_id = ? ORDER BY id";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, passoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    if (tarefaMap.containsKey(id)) {
                        list.add(tarefaMap.get(id));
                    } else {
                        Tarefa t = map(rs);
                        tarefaMap.put(t.getId(), t);
                        list.add(t);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Tarefa> findAllByProduto(int produtoId) {
        List<Tarefa> list = new ArrayList<>();
        String sql = "SELECT * FROM Tarefa WHERE produto_id = ? ORDER BY id";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, produtoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    if (tarefaMap.containsKey(id)) {
                        list.add(tarefaMap.get(id));
                    } else {
                        Tarefa t = map(rs);
                        tarefaMap.put(t.getId(), t);
                        list.add(t);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Tarefa WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) tarefaMap.remove(id);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
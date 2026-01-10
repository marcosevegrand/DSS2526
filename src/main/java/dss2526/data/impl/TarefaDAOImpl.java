package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.TarefaDAO;
import dss2526.domain.entity.Tarefa;
import dss2526.domain.enumeration.EstadoTarefa;

import java.sql.*;
import java.util.*;

/**
 * Implementação do TarefaDAO com suporte completo para persistência de estacao_id.
 * Utiliza padrão Singleton e Identity Map para otimização.
 */
public class TarefaDAOImpl implements TarefaDAO {
    private static TarefaDAOImpl instance;
    private final DBConfig dbConfig;

    // Identity Map para Tarefa
    private final Map<Integer, Tarefa> tarefaMap = new HashMap<>();

    private TarefaDAOImpl() {
        this.dbConfig = DBConfig.getInstance();
    }

    public static synchronized TarefaDAOImpl getInstance() {
        if (instance == null) {
            instance = new TarefaDAOImpl();
        }
        return instance;
    }

    @Override
    public Tarefa create(Tarefa entity) {
        String sql = "INSERT INTO Tarefa (passo_id, produto_id, pedido_id, estacao_id, estado, data_criacao, data_inicio, data_conclusao) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entity.getPassoId());
            ps.setInt(2, entity.getProdutoId());
            ps.setInt(3, entity.getPedidoId());

            // CORRIGIDO: Persistir estacao_id (pode ser NULL para tarefas pendentes)
            if (entity.getEstacaoId() > 0) {
                ps.setInt(4, entity.getEstacaoId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setString(5, entity.getEstado().name());
            ps.setTimestamp(6, Timestamp.valueOf(entity.getDataCriacao()));

            if (entity.getDataInicio() != null) {
                ps.setTimestamp(7, Timestamp.valueOf(entity.getDataInicio()));
            } else {
                ps.setNull(7, Types.TIMESTAMP);
            }

            if (entity.getDataConclusao() != null) {
                ps.setTimestamp(8, Timestamp.valueOf(entity.getDataConclusao()));
            } else {
                ps.setNull(8, Types.TIMESTAMP);
            }

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
        String sql = "UPDATE Tarefa SET passo_id=?, produto_id=?, pedido_id=?, estacao_id=?, estado=?, data_criacao=?, data_inicio=?, data_conclusao=? WHERE id=?";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, entity.getPassoId());
            ps.setInt(2, entity.getProdutoId());
            ps.setInt(3, entity.getPedidoId());

            // CORRIGIDO: Persistir estacao_id
            if (entity.getEstacaoId() > 0) {
                ps.setInt(4, entity.getEstacaoId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setString(5, entity.getEstado().name());
            ps.setTimestamp(6, Timestamp.valueOf(entity.getDataCriacao()));

            if (entity.getDataInicio() != null) {
                ps.setTimestamp(7, Timestamp.valueOf(entity.getDataInicio()));
            } else {
                ps.setNull(7, Types.TIMESTAMP);
            }

            if (entity.getDataConclusao() != null) {
                ps.setTimestamp(8, Timestamp.valueOf(entity.getDataConclusao()));
            } else {
                ps.setNull(8, Types.TIMESTAMP);
            }

            ps.setInt(9, entity.getId());

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

    /**
     * Mapeia um ResultSet para uma entidade Tarefa.
     */
    private Tarefa map(ResultSet rs) throws SQLException {
        Tarefa t = new Tarefa();
        t.setId(rs.getInt("id"));
        t.setPassoId(rs.getInt("passo_id"));
        t.setProdutoId(rs.getInt("produto_id"));
        t.setPedidoId(rs.getInt("pedido_id"));

        // CORRIGIDO: Carregar estacao_id da base de dados
        int estacaoId = rs.getInt("estacao_id");
        if (!rs.wasNull()) {
            t.setEstacaoId(estacaoId);
        }

        t.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());

        Timestamp tsInicio = rs.getTimestamp("data_inicio");
        if (tsInicio != null) {
            t.setDataInicio(tsInicio.toLocalDateTime());
        }

        Timestamp tsConclusao = rs.getTimestamp("data_conclusao");
        if (tsConclusao != null) {
            t.setDataConclusao(tsConclusao.toLocalDateTime());
        }

        // Mapeia a String da BD para o Enum
        String estadoStr = rs.getString("estado");
        if (estadoStr != null) {
            try {
                t.setEstado(EstadoTarefa.valueOf(estadoStr));
            } catch (IllegalArgumentException e) {
                t.setEstado(EstadoTarefa.PENDENTE);
            }
        } else {
            t.setEstado(EstadoTarefa.PENDENTE);
        }

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
            if (rows > 0) {
                tarefaMap.remove(id);
            }
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
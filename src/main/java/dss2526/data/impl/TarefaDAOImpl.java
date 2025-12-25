package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.TarefaDAO;
import dss2526.domain.entity.Tarefa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TarefaDAOImpl implements TarefaDAO {
    private static TarefaDAOImpl instance;
    private final DBConfig dbConfig = DBConfig.getInstance();

    public static TarefaDAOImpl getInstance() {
        if(instance == null) instance = new TarefaDAOImpl();
        return instance;
    }

    private TarefaDAOImpl() {}

    @Override
    public Tarefa create(Tarefa t) {
        String sql = "INSERT INTO Tarefa (PassoId, PedidoId, Concluido) VALUES (?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, t.getIdPasso());
            stmt.setInt(2, t.getIdPedido());
            stmt.setBoolean(3, t.isConcluido());
            
            stmt.executeUpdate();
            try(ResultSet rs = stmt.getGeneratedKeys()){
                if(rs.next()) t.setId(rs.getInt(1));
            }
            return t;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Tarefa findById(Integer id) {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Tarefa WHERE Id=?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseTarefa(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Tarefa update(Tarefa t) {
        String sql = "UPDATE Tarefa SET PassoId=?, PedidoId=?, Concluido=? WHERE Id=?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, t.getIdPasso());
            stmt.setInt(2, t.getIdPedido());
            stmt.setBoolean(3, t.isConcluido());
            stmt.setInt(4, t.getId());
            
            stmt.executeUpdate();
            return t;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Tarefa WHERE Id=?")) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Tarefa> findAll() {
        List<Tarefa> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Tarefa");
             ResultSet rs = stmt.executeQuery()) {
            while(rs.next()) {
                list.add(parseTarefa(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Tarefa> findByEstado(boolean concluida) {
        List<Tarefa> list = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Tarefa WHERE Concluido=?")) {
            stmt.setBoolean(1, concluida);
            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    list.add(parseTarefa(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Tarefa parseTarefa(ResultSet rs) throws SQLException {
        Tarefa t = new Tarefa();
        t.setId(rs.getInt("Id"));
        t.setIdPasso(rs.getInt("PassoId"));
        t.setIdPedido(rs.getInt("PedidoId"));
        t.setConcluido(rs.getBoolean("Concluido"));
        return t;
    }
}
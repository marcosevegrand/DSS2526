package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.TarefaDAO;
import dss2526.domain.entity.Tarefa;
import dss2526.domain.enumeration.Trabalho;

import java.sql.*;
import java.util.*;

public class TarefaDAOImpl implements TarefaDAO {

    private DBConfig dbConfig = DBConfig.getInstance();
    private static Map<Integer, Tarefa> identityMap = new HashMap<>();

    // --- Métodos de compatibilidade (Estilo Map) ---
    @Override public Tarefa get(int id) { return findById(id); }
    @Override public Collection<Tarefa> values() { return findAll(); }
    
    @Override 
    public void put(int id, Tarefa t) {
        if (id > 0 && findById(id) != null) update(t);
        else save(t);
    }

    @Override
    public Tarefa save(Tarefa t) {
        String sql = "INSERT INTO Tarefa (PedidoID, RestauranteID, Nome, Trabalho, Concluida, DataCriacao) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, t.getPedidoId());
            ps.setInt(2, t.getRestauranteId());
            ps.setString(3, t.getNome());
            ps.setString(4, t.getTrabalho() != null ? t.getTrabalho().name() : null);
            ps.setBoolean(5, t.isConcluida());
            ps.setTimestamp(6, Timestamp.valueOf(t.getDataCriacao()));
            
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    t.setId(rs.getInt(1));
                }
            }
            identityMap.put(t.getId(), t);
        } catch (SQLException e) { e.printStackTrace(); }
        return t;
    }

    @Override
    public Tarefa update(Tarefa t) {
        String sql = "UPDATE Tarefa SET Concluida = ?, DataFim = ? WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setBoolean(1, t.isConcluida());
            ps.setTimestamp(2, t.getDataFim() != null ? Timestamp.valueOf(t.getDataFim()) : null);
            ps.setInt(3, t.getId());
            
            ps.executeUpdate();
            identityMap.put(t.getId(), t);
        } catch (SQLException e) { e.printStackTrace(); }
        return t;
    }

    @Override
    public Tarefa findById(Integer id) { // Mantido Integer apenas para bater com a assinatura do GenericDAO se necessário, mas tratado como int
        if (id == null || id <= 0) return null;
        if (identityMap.containsKey(id)) return identityMap.get(id);

        String sql = "SELECT * FROM Tarefa WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Tarefa t = new Tarefa();
                    t.setId(rs.getInt("ID"));
                    t.setPedidoId(rs.getInt("PedidoID"));
                    t.setRestauranteId(rs.getInt("RestauranteID"));
                    t.setNome(rs.getString("Nome"));
                    
                    String trab = rs.getString("Trabalho");
                    if (trab != null) t.setTrabalho(Trabalho.valueOf(trab));
                    
                    t.setConcluida(rs.getBoolean("Concluida"));
                    t.setDataCriacao(rs.getTimestamp("DataCriacao").toLocalDateTime());
                    
                    Timestamp fim = rs.getTimestamp("DataFim");
                    if (fim != null) t.setDataFim(fim.toLocalDateTime());
                    
                    identityMap.put(t.getId(), t);
                    return t;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<Tarefa> findAll() {
        List<Tarefa> result = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT ID FROM Tarefa")) {
            while (rs.next()) {
                result.add(findById(rs.getInt(1)));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    @Override
    public boolean delete(Integer id) {
        if (id == null || id <= 0) return false;
        String sql = "DELETE FROM Tarefa WHERE ID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            identityMap.remove(id);
            return rows > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // --- Métodos de Filtro por int ---
    @Override
    public List<Tarefa> findByRestaurante(int restauranteId) {
        List<Tarefa> res = new ArrayList<>();
        String sql = "SELECT ID FROM Tarefa WHERE RestauranteID = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, restauranteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) res.add(findById(rs.getInt("ID")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return res;
    }

    @Override
    public List<Tarefa> findPendentesByRestaurante(int restauranteId) {
        List<Tarefa> res = new ArrayList<>();
        String sql = "SELECT ID FROM Tarefa WHERE RestauranteID = ? AND Concluida = 0";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, restauranteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) res.add(findById(rs.getInt("ID")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return res;
    }
}
package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.PassoDAO;
import dss2526.domain.entity.Passo;
import dss2526.domain.enumeration.Trabalho;

import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PassoDAOImpl implements PassoDAO {

    private DBConfig dbConfig = DBConfig.getInstance();
    private static Map<Integer, Passo> identityMap = new HashMap<>();

    @Override
    public Passo save(Passo p) {
        String sql = (p.getId() > 0) ?
            "INSERT INTO Passos (ID, Nome, Duracao, Trabalho) VALUES (?, ?, ?, ?)" :
            "INSERT INTO Passos (Nome, Duracao, Trabalho) VALUES (?, ?, ?)";

        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            int idx = 1;
            if (p.getId() > 0) pstmt.setInt(idx++, p.getId());
            pstmt.setString(idx++, p.getNome());
            pstmt.setLong(idx++, p.getDuracao().getSeconds());
            pstmt.setString(idx++, p.getTrabalho() != null ? p.getTrabalho().name() : null);
            
            pstmt.executeUpdate();
            
            if (p.getId() == 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) p.setId(rs.getInt(1));
                }
            }
            identityMap.put(p.getId(), p);
            return p;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Passo findById(Integer id) {
        if (identityMap.containsKey(id)) return identityMap.get(id);

        String sql = "SELECT * FROM Passos WHERE ID = ?";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Passo p = new Passo();
                    p.setId(rs.getInt("ID"));
                    p.setNome(rs.getString("Nome"));
                    p.setDuracao(Duration.ofSeconds(rs.getLong("Duracao")));
                    String trab = rs.getString("Trabalho");
                    if (trab != null) p.setTrabalho(Trabalho.valueOf(trab));
                    identityMap.put(p.getId(), p);
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Passo> findAll() {
        List<Passo> lista = new ArrayList<>();
        String sql = "SELECT ID FROM Passos";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) lista.add(findById(rs.getInt("ID")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Passo update(Passo p) {
        String sql = "UPDATE Passos SET Nome = ?, Duracao = ?, Trabalho = ? WHERE ID = ?";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getNome());
            pstmt.setLong(2, p.getDuracao().getSeconds());
            pstmt.setString(3, p.getTrabalho() != null ? p.getTrabalho().name() : null);
            pstmt.setInt(4, p.getId());
            pstmt.executeUpdate();
            identityMap.put(p.getId(), p);
            return p;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Passos WHERE ID = ?";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            if (pstmt.executeUpdate() > 0) {
                identityMap.remove(id);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
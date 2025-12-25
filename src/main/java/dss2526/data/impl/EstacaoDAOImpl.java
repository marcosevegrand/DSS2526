package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.EstacaoDAO;
import dss2526.domain.entity.Estacao;
import dss2526.domain.enumeration.Trabalho;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstacaoDAOImpl implements EstacaoDAO {

    private DBConfig dbConfig = DBConfig.getInstance();
    private static Map<Integer, Estacao> identityMap = new HashMap<>();

    @Override
    public Estacao save(Estacao estacao) {
        String sql = (estacao.getId() > 0) ? 
            "INSERT INTO Estacoes (ID, Restaurante_ID, Trabalho) VALUES (?, ?, ?)" :
            "INSERT INTO Estacoes (Restaurante_ID, Trabalho) VALUES (?, ?)";
            
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            if (estacao.getId() > 0) {
                pstmt.setInt(1, estacao.getId());
                pstmt.setInt(2, estacao.getRestauranteId());
                pstmt.setString(3, estacao.getTrabalho() != null ? estacao.getTrabalho().name() : null);
            } else {
                pstmt.setInt(1, estacao.getRestauranteId());
                pstmt.setString(2, estacao.getTrabalho() != null ? estacao.getTrabalho().name() : null);
            }
            
            pstmt.executeUpdate();
            
            if (estacao.getId() == 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) estacao.setId(rs.getInt(1));
                }
            }
            identityMap.put(estacao.getId(), estacao);
            return estacao;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Estacao findById(Integer id) {
        if (identityMap.containsKey(id)) return identityMap.get(id);

        String sql = "SELECT * FROM Estacoes WHERE ID = ?";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Estacao e = new Estacao();
                    e.setId(rs.getInt("ID"));
                    e.setRestauranteId(rs.getInt("Restaurante_ID"));
                    String trab = rs.getString("Trabalho");
                    if (trab != null) e.setTrabalho(Trabalho.valueOf(trab));
                    identityMap.put(e.getId(), e);
                    return e;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Estacao> findAll() {
        List<Estacao> lista = new ArrayList<>();
        String sql = "SELECT ID FROM Estacoes";
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
    public Estacao update(Estacao estacao) {
        String sql = "UPDATE Estacoes SET Restaurante_ID = ?, Trabalho = ? WHERE ID = ?";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, estacao.getRestauranteId());
            pstmt.setString(2, estacao.getTrabalho() != null ? estacao.getTrabalho().name() : null);
            pstmt.setInt(3, estacao.getId());
            pstmt.executeUpdate();
            identityMap.put(estacao.getId(), estacao);
            return estacao;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Estacoes WHERE ID = ?";
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
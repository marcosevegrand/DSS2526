package pt.uminho.dss.restaurante.persistence.impl;

import pt.uminho.dss.restaurante.domain.entity.Ingrediente;
import pt.uminho.dss.restaurante.domain.enumeration.Alergenico;
import pt.uminho.dss.restaurante.persistence.config.DBConfig;
import pt.uminho.dss.restaurante.persistence.contract.IngredienteDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngredienteDAOImpl implements IngredienteDAO {

    @Override
    public void put(Integer key, Ingrediente value) {
        String sqlCheck = "SELECT count(*) FROM ingredientes WHERE id = ?";
        try (Connection conn = DBConfig.getConnection();
                PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {

            psCheck.setInt(1, key);
            ResultSet rs = psCheck.executeQuery();
            boolean exists = false;
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }

            if (exists) {
                // Update
                String sqlUpdate = "UPDATE ingredientes SET nome = ?, unidade_medida = ?, alergenico = ? WHERE id = ?";
                try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                    psUpdate.setString(1, value.getNome());
                    psUpdate.setString(2, value.getUnidadeMedida());
                    psUpdate.setString(3, value.getAlergenico() != null ? value.getAlergenico().name() : null);
                    psUpdate.setInt(4, key);
                    psUpdate.executeUpdate();
                }
            } else {
                // Insert
                String sqlInsert = "INSERT INTO ingredientes (id, nome, unidade_medida, alergenico) VALUES (?, ?, ?, ?)";
                try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                    psInsert.setInt(1, key);
                    psInsert.setString(2, value.getNome());
                    psInsert.setString(3, value.getUnidadeMedida());
                    psInsert.setString(4, value.getAlergenico() != null ? value.getAlergenico().name() : null);
                    psInsert.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir/atualizar ingrediente", e);
        }
    }

    @Override
    public Ingrediente get(Integer key) {
        String sql = "SELECT * FROM ingredientes WHERE id = ?";
        try (Connection conn = DBConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return buildIngrediente(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar ingrediente", e);
        }
        return null;
    }

    @Override
    public Ingrediente remove(Integer key) {
        Ingrediente toReturn = get(key);
        if (toReturn != null) {
            String sql = "DELETE FROM ingredientes WHERE id = ?";
            try (Connection conn = DBConfig.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, key);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao remover ingrediente", e);
            }
        }
        return toReturn;
    }

    @Override
    public boolean containsKey(Integer key) {
        String sql = "SELECT 1 FROM ingredientes WHERE id = ?";
        try (Connection conn = DBConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar ingrediente", e);
        }
    }

    @Override
    public List<Ingrediente> values() {
        List<Ingrediente> list = new ArrayList<>();
        String sql = "SELECT * FROM ingredientes";
        try (Connection conn = DBConfig.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(buildIngrediente(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar ingredientes", e);
        }
        return list;
    }

    @Override
    public int size() {
        String sql = "SELECT count(*) FROM ingredientes";
        try (Connection conn = DBConfig.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar ingredientes", e);
        }
        return 0;
    }

    private Ingrediente buildIngrediente(ResultSet rs) throws SQLException {
        Ingrediente i = new Ingrediente();
        i.setId(rs.getInt("id"));
        i.setNome(rs.getString("nome"));
        i.setUnidadeMedida(rs.getString("unidade_medida"));
        String alg = rs.getString("alergenico");
        if (alg != null) {
            i.setAlergenico(Alergenico.valueOf(alg));
        }
        return i;
    }
}
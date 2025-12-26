package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.RestauranteDAO;
import dss2526.domain.entity.LinhaStock;
import dss2526.domain.entity.Restaurante;

import java.sql.*;
import java.util.*;

public class RestauranteDAOImpl implements RestauranteDAO {
    private static RestauranteDAOImpl instance;
    private DBConfig dbConfig;

    // Identity Map for Restaurante
    private Map<Integer, Restaurante> restauranteMap = new HashMap<>();

    // Identity Map for LinhaStock
    private Map<Integer, LinhaStock> linhaStockMap = new HashMap<>();

    private RestauranteDAOImpl() {
        this.dbConfig = DBConfig.getInstance();
    }

    public static synchronized RestauranteDAOImpl getInstance() {
        if (instance == null) instance = new RestauranteDAOImpl();
        return instance;
    }

    @Override
    public Restaurante create(Restaurante entity) {
        String sql = "INSERT INTO Restaurante (nome, localizacao, catalogo_id) VALUES (?, ?, ?)";
        String sqlStock = "INSERT INTO LinhaStock (restaurante_id, ingrediente_id, quantidade) VALUES (?, ?, ?)";

        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, entity.getNome());
                ps.setString(2, entity.getLocalizacao());
                if (entity.getCatalogoId() != null) ps.setInt(3, entity.getCatalogoId());
                else ps.setNull(3, Types.INTEGER);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setId(rs.getInt(1));
                        restauranteMap.put(entity.getId(), entity);
                    }
                }

                try (PreparedStatement psS = conn.prepareStatement(sqlStock, Statement.RETURN_GENERATED_KEYS)) {
                    for (LinhaStock ls : entity.getStock()) {
                        psS.setInt(1, entity.getId());
                        psS.setInt(2, ls.getIngredienteId());
                        psS.setDouble(3, ls.getQuantidade());
                        psS.executeUpdate();
                        try (ResultSet rs = psS.getGeneratedKeys()) {
                            if (rs.next()) {
                                ls.setId(rs.getInt(1));
                                ls.setRestauranteId(entity.getId());
                                linhaStockMap.put(ls.getId(), ls);
                            }
                        }
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Restaurante update(Restaurante entity) {
        String sql = "UPDATE Restaurante SET nome=?, localizacao=?, catalogo_id=? WHERE id=?";
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, entity.getNome());
                ps.setString(2, entity.getLocalizacao());
                if (entity.getCatalogoId() != null) ps.setInt(3, entity.getCatalogoId());
                else ps.setNull(3, Types.INTEGER);
                ps.setInt(4, entity.getId());
                ps.executeUpdate();

                // Re-insert stock
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("DELETE FROM LinhaStock WHERE restaurante_id=" + entity.getId());
                }
                String sqlStock = "INSERT INTO LinhaStock (restaurante_id, ingrediente_id, quantidade) VALUES (?, ?, ?)";
                try (PreparedStatement psS = conn.prepareStatement(sqlStock, Statement.RETURN_GENERATED_KEYS)) {
                    for (LinhaStock ls : entity.getStock()) {
                        psS.setInt(1, entity.getId());
                        psS.setInt(2, ls.getIngredienteId());
                        psS.setDouble(3, ls.getQuantidade());
                        psS.executeUpdate();
                        try (ResultSet rs = psS.getGeneratedKeys()) {
                            if (rs.next()) {
                                ls.setId(rs.getInt(1));
                                ls.setRestauranteId(entity.getId());
                                linhaStockMap.put(ls.getId(), ls);
                            }
                        }
                    }
                }
                conn.commit();
                restauranteMap.put(entity.getId(), entity);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Restaurante findById(Integer id) {
        if (restauranteMap.containsKey(id)) {
            return restauranteMap.get(id);
        }

        String sql = "SELECT * FROM Restaurante WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Restaurante r = new Restaurante();
                    r.setId(rs.getInt("id"));
                    r.setNome(rs.getString("nome"));
                    r.setLocalizacao(rs.getString("localizacao"));
                    int catId = rs.getInt("catalogo_id");
                    if (!rs.wasNull()) r.setCatalogoId(catId);
                    
                    restauranteMap.put(r.getId(), r);
                    
                    r.setStock(findStock(conn, id));
                    
                    // Fetch referenced IDs (independent entities)
                    r.setEstacaoIds(findChildIds(conn, "Estacao", id));
                    r.setFuncionarioIds(findChildIds(conn, "Funcionario", id));
                    r.setPedidoIds(findChildIds(conn, "Pedido", id));
                    
                    return r;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<LinhaStock> findStock(Connection conn, int restId) throws SQLException {
        List<LinhaStock> list = new ArrayList<>();
        String sql = "SELECT * FROM LinhaStock WHERE restaurante_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, restId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    if (linhaStockMap.containsKey(id)) {
                        list.add(linhaStockMap.get(id));
                    } else {
                        LinhaStock ls = new LinhaStock();
                        ls.setId(id);
                        ls.setRestauranteId(rs.getInt("restaurante_id"));
                        ls.setIngredienteId(rs.getInt("ingrediente_id"));
                        ls.setQuantidade(rs.getInt("quantidade"));
                        linhaStockMap.put(id, ls);
                        list.add(ls);
                    }
                }
            }
        }
        return list;
    }

    private List<Integer> findChildIds(Connection conn, String table, int restId) throws SQLException {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT id FROM " + table + " WHERE restaurante_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, restId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(rs.getInt("id"));
            }
        }
        return list;
    }

    @Override
    public List<Restaurante> findAll() {
        List<Restaurante> list = new ArrayList<>();
        String sql = "SELECT id FROM Restaurante";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(findById(rs.getInt("id")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Restaurante> findAllByCatalogo(int catalogoId) {
        List<Restaurante> list = new ArrayList<>();
        String sql = "SELECT id FROM Restaurante WHERE catalogo_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, catalogoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(findById(rs.getInt("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Restaurante WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) restauranteMap.remove(id);
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
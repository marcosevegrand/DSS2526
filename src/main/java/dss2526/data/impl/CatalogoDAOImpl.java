package dss2526.data.impl;

import dss2526.data.DBConfig;
import dss2526.data.contract.CatalogoDAO;
import dss2526.domain.entity.Catalogo;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalogoDAOImpl implements CatalogoDAO {

    private DBConfig dbConfig = DBConfig.getInstance();
    private static Map<Integer, Catalogo> identityMap = new HashMap<>();

    @Override
    public Catalogo save(Catalogo catalogo) {
        String sql = "INSERT INTO Catalogos (ID) VALUES (?)"; 
        
        if (catalogo.getId() > 0) {
            try (Connection conn = this.dbConfig.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, catalogo.getId());
                pstmt.executeUpdate();
                identityMap.put(catalogo.getId(), catalogo);
                return catalogo;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        } else {
             sql = "INSERT INTO Catalogos DEFAULT VALUES";
             try (Connection conn = this.dbConfig.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        catalogo.setId(rs.getInt(1));
                        identityMap.put(catalogo.getId(), catalogo);
                    }
                }
                return catalogo;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public Catalogo findById(Integer id) {
        if (identityMap.containsKey(id)) return identityMap.get(id);

        String sql = "SELECT * FROM Catalogos WHERE ID = ?";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Catalogo c = new Catalogo();
                    c.setId(rs.getInt("ID"));
                    
                    // A nova estrutura do Catalogo tem listas separadas.
                    // Idealmente, deve-se usar ProdutoDAO e MenuDAO aqui para popular as listas.
                    // Exemplo: 
                    // ProdutoDAO produtoDAO = new ProdutoDAOImpl();
                    // c.setProdutos(produtoDAO.findAll()); // Se o catálogo contiver todos os produtos
                    
                    // MenuDAO menuDAO = new MenuDAOImpl();
                    // c.setMenus(menuDAO.findAll());
                    
                    identityMap.put(c.getId(), c);
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Catalogo> findAll() {
        List<Catalogo> lista = new ArrayList<>();
        String sql = "SELECT ID FROM Catalogos";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(findById(rs.getInt("ID")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Catalogo update(Catalogo catalogo) {
        // Catalogo entity agora tem listas separadas, mas a tabela Catalogos mantém apenas o ID.
        // Se houver tabelas de associação, o update destas listas deve ser gerido aqui ou nos DAOs específicos.
        identityMap.put(catalogo.getId(), catalogo);
        return catalogo;
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Catalogos WHERE ID = ?";
        try (Connection conn = this.dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                identityMap.remove(id);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
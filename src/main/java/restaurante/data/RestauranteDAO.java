package restaurante.data;

import restaurante.business.pedidos.Pedido;
import restaurante.business.pedidos.Alimento;
import restaurante.business.funcionarios.Funcionario;
import restaurante.business.restaurantes.Restaurante;
import restaurante.data.IRestauranteDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestauranteDAO implements IRestauranteDAO {

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DAOConfig.URL,
                DAOConfig.USERNAME,
                DAOConfig.PASSWORD
        );
    }

    /* ===================== PEDIDO ===================== */

    @Override
    public void guardarPedido(Pedido p) {
        String sql =
                "INSERT INTO Pedido (id, data, estado, precoTotal, terminal_venda_id, cliente_id) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "  data = VALUES(data), " +
                "  estado = VALUES(estado), " +
                "  precoTotal = VALUES(precoTotal), " +
                "  terminal_venda_id = VALUES(terminal_venda_id), " +
                "  cliente_id = VALUES(cliente_id)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            // ATENÇÃO: adapta estes métodos aos que existem em Pedido
            ps.setInt(1, p.getId());
            ps.setTimestamp(2, new Timestamp(p.getData().getTime())); // se p.getData() é java.util.Date
            ps.setString(3, p.getEstado().name());                    // se Estado é enum; se for String, usa getEstado()
            ps.setBigDecimal(4, p.getPrecoTotal());
            ps.setInt(5, p.getTerminalVendaId());
            if (p.getClienteId() == null) ps.setNull(6, Types.INTEGER);
            else ps.setInt(6, p.getClienteId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro a guardar pedido", e);
        }
    }

    @Override
    public Pedido obterPedido(int id) {
        String sql =
                "SELECT id, data, estado, precoTotal, terminal_venda_id, cliente_id " +
                "FROM Pedido WHERE id = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapPedido(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro a obter pedido", e);
        }
    }

    @Override
    public List<Pedido> obterTodosPedidos() {
        String sql = "SELECT id, data, estado, precoTotal, terminal_venda_id, cliente_id FROM Pedido";
        List<Pedido> res = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) res.add(mapPedido(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro a listar pedidos", e);
        }
        return res;
    }

    @Override
    public void atualizarPedido(Pedido p) {
        String sql =
                "UPDATE Pedido " +
                "SET data = ?, estado = ?, precoTotal = ?, terminal_venda_id = ?, cliente_id = ? " +
                "WHERE id = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setTimestamp(1, new Timestamp(p.getData().getTime()));
            ps.setString(2, p.getEstado().name());
            ps.setBigDecimal(3, p.getPrecoTotal());
            ps.setInt(4, p.getTerminalVendaId());
            if (p.getClienteId() == null) ps.setNull(5, Types.INTEGER);
            else ps.setInt(5, p.getClienteId());
            ps.setInt(6, p.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro a atualizar pedido", e);
        }
    }

    @Override
    public void removerPedido(int id) {
        String sql = "DELETE FROM Pedido WHERE id = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro a remover pedido", e);
        }
    }

    private Pedido mapPedido(ResultSet rs) throws SQLException {
        // ATENÇÃO: certifica-te que Pedido tem construtor vazio e estes setters
        Pedido p = new Pedido();
        p.setId(rs.getInt("id"));
        // se a classe usa java.util.Date:
        p.setData(rs.getTimestamp("data"));
        // se Estado é enum:
        p.setEstado(Pedido.Estado.valueOf(rs.getString("estado")));
        p.setPrecoTotal(rs.getBigDecimal("precoTotal"));
        p.setTerminalVendaId(rs.getInt("terminal_venda_id"));
        int cid = rs.getInt("cliente_id");
        p.setClienteId(rs.wasNull() ? null : cid);
        return p;
    }

    /* ===================== ALIMENTO ===================== */

    @Override
    public void guardarAlimento(Alimento a) {
        String sql =
                "INSERT INTO Alimento (id, nome, preco, tipo, descricao) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "  nome = VALUES(nome), " +
                "  preco = VALUES(preco), " +
                "  tipo = VALUES(tipo), " +
                "  descricao = VALUES(descricao)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, a.getId());
            ps.setString(2, a.getNome());
            // se tiveres uma classe Preco, converte aqui:
            ps.setBigDecimal(3, a.getPreco());
            // se Tipo for enum:
            ps.setString(4, a.getTipo().name());
            ps.setString(5, a.getDescricao());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro a guardar alimento", e);
        }
    }

    @Override
    public Alimento obterAlimento(String id) {
        String sql =
                "SELECT id, nome, preco, tipo, descricao " +
                "FROM Alimento WHERE id = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapAlimento(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro a obter alimento", e);
        }
    }

    @Override
    public List<Alimento> obterTodosAlimentos() {
        String sql = "SELECT id, nome, preco, tipo, descricao FROM Alimento";
        List<Alimento> res = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) res.add(mapAlimento(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro a listar alimentos", e);
        }
        return res;
    }

    @Override
    public void atualizarAlimento(Alimento a) {
        String sql =
                "UPDATE Alimento " +
                "SET nome = ?, preco = ?, tipo = ?, descricao = ? " +
                "WHERE id = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, a.getNome());
            ps.setBigDecimal(2, a.getPreco());
            ps.setString(3, a.getTipo().name());
            ps.setString(4, a.getDescricao());
            ps.setString(5, a.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro a atualizar alimento", e);
        }
    }

    @Override
    public void removerAlimento(String id) {
        String sql = "DELETE FROM Alimento WHERE id = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro a remover alimento", e);
        }
    }

    private Alimento mapAlimento(ResultSet rs) throws SQLException {
        Alimento a = new Alimento();
        a.setId(rs.getString("id"));
        a.setNome(rs.getString("nome"));
        a.setPreco(rs.getBigDecimal("preco"));
        a.setTipo(Alimento.Tipo.valueOf(rs.getString("tipo")));
        a.setDescricao(rs.getString("descricao"));
        return a;
    }

    /* ===================== FUNCIONARIO ===================== */

    @Override
    public void guardarFuncionario(Funcionario f) {
        String sql =
                "INSERT INTO Funcionario (id, nome, cargo, username, password, restaurante_id) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "  nome = VALUES(nome), " +
                "  cargo = VALUES(cargo), " +
                "  username = VALUES(username), " +
                "  password = VALUES(password), " +
                "  restaurante_id = VALUES(restaurante_id)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, f.getId());
            ps.setString(2, f.getNome());
            ps.setString(3, f.getCargo().name());   // se cargo for enum; se for String, remove .name()
            ps.setString(4, f.getUsername());
            ps.setString(5, f.getPassword());
            ps.setInt(6, f.getRestauranteId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro a guardar funcionário", e);
        }
    }

    @Override
    public Funcionario obterFuncionario(int id) {
        String sql =
                "SELECT id, nome, cargo, username, password, restaurante_id " +
                "FROM Funcionario WHERE id = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapFuncionario(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro a obter funcionário", e);
        }
    }

    @Override
    public Funcionario obterFuncionarioPorUsername(String username) {
        String sql =
                "SELECT id, nome, cargo, username, password, restaurante_id " +
                "FROM Funcionario WHERE username = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapFuncionario(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro a obter funcionário por username", e);
        }
    }

    @Override
    public List<Funcionario> obterTodosFuncionarios() {
        String sql = "SELECT id, nome, cargo, username, password, restaurante_id FROM Funcionario";
        List<Funcionario> res = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) res.add(mapFuncionario(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro a listar funcionários", e);
        }
        return res;
    }

    @Override
    public void atualizarFuncionario(Funcionario f) {
        String sql =
                "UPDATE Funcionario " +
                "SET nome = ?, cargo = ?, username = ?, password = ?, restaurante_id = ? " +
                "WHERE id = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, f.getNome());
            ps.setString(2, f.getCargo().name());
            ps.setString(3, f.getUsername());
            ps.setString(4, f.getPassword());
            ps.setInt(5, f.getRestauranteId());
            ps.setInt(6, f.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro a atualizar funcionário", e);
        }
    }

    @Override
    public void removerFuncionario(int id) {
        String sql = "DELETE FROM Funcionario WHERE id = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro a remover funcionário", e);
        }
    }

    private Funcionario mapFuncionario(ResultSet rs) throws SQLException {
        Funcionario f = new Funcionario();
        f.setId(rs.getInt("id"));
        f.setNome(rs.getString("nome"));
        f.setCargo(Funcionario.Cargo.valueOf(rs.getString("cargo")));
        f.setUsername(rs.getString("username"));
        f.setPassword(rs.getString("password"));
        f.setRestauranteId(rs.getInt("restaurante_id"));
        return f;
    }

    /* ===================== RESTAURANTE ===================== */

    @Override
    public void guardarRestaurante(Restaurante r) {
        String sql =
                "INSERT INTO Restaurante (id, nome, morada, cadeia_id) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "  nome = VALUES(nome), " +
                "  morada = VALUES(morada), " +
                "  cadeia_id = VALUES(cadeia_id)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, r.getId());
            ps.setString(2, r.getNome());
            ps.setString(3, r.getMorada());
            ps.setInt(4, r.getCadeiaId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro a guardar restaurante", e);
        }
    }

    @Override
    public Restaurante obterRestaurante(int id) {
        String sql = "SELECT id, nome, morada, cadeia_id FROM Restaurante WHERE id = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRestaurante(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro a obter restaurante", e);
        }
    }

    @Override
    public List<Restaurante> obterTodosRestaurantes() {
        String sql = "SELECT id, nome, morada, cadeia_id FROM Restaurante";
        List<Restaurante> res = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) res.add(mapRestaurante(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro a listar restaurantes", e);
        }
        return res;
    }

    @Override
    public void atualizarRestaurante(Restaurante r) {
        String sql =
                "UPDATE Restaurante " +
                "SET nome = ?, morada = ?, cadeia_id = ? " +
                "WHERE id = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, r.getNome());
            ps.setString(2, r.getMorada());
            ps.setInt(3, r.getCadeiaId());
            ps.setInt(4, r.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro a atualizar restaurante", e);
        }
    }

    @Override
    public void removerRestaurante(int id) {
        String sql = "DELETE FROM Restaurante WHERE id = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro a remover restaurante", e);
        }
    }

    private Restaurante mapRestaurante(ResultSet rs) throws SQLException {
        Restaurante r = new Restaurante();
        r.setId(rs.getInt("id"));
        r.setNome(rs.getString("nome"));
        r.setMorada(rs.getString("morada"));
        r.setCadeiaId(rs.getInt("cadeia_id"));
        return r;
    }
}

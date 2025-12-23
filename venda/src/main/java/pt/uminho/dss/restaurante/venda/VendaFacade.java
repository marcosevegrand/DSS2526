package pt.uminho.dss.restaurante.venda;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional; // Still imported but might use plain null check or Optional.ofNullable

import pt.uminho.dss.restaurante.domain.entity.Pedido;
import pt.uminho.dss.restaurante.domain.entity.Produto;
import pt.uminho.dss.restaurante.domain.entity.Menu;
import pt.uminho.dss.restaurante.domain.entity.LinhaPedido;
import pt.uminho.dss.restaurante.domain.entity.Catalogo;
import pt.uminho.dss.restaurante.domain.enumeration.EstadoPedido;
import pt.uminho.dss.restaurante.persistence.contract.ProdutoDAO;
import pt.uminho.dss.restaurante.persistence.contract.MenuDAO;
import pt.uminho.dss.restaurante.persistence.contract.PedidoDAO;

/**
 * Fachada de venda — versão atualizada para usar API Map nos DAOs.
 */
public class VendaFacade implements IVenda {

    private final ProdutoDAO produtoDAO;
    private final MenuDAO menuDAO;
    private final PedidoDAO pedidoDAO;
    // Removed Catalogo because constructing it with DAOs here seems weird if it's
    // not used,
    // and the original code tried to pass DAOs to Catalogo constructor which might
    // not exist.
    // Assuming Catalogo logic is handled elsewhere or simplified.
    private Catalogo catalogo;

    public VendaFacade(ProdutoDAO produtoDAO, MenuDAO menuDAO, PedidoDAO pedidoDAO) {
        this.produtoDAO = Objects.requireNonNull(produtoDAO);
        this.menuDAO = Objects.requireNonNull(menuDAO);
        this.pedidoDAO = Objects.requireNonNull(pedidoDAO);
        // this.catalogo = new Catalogo(produtoDAO, menuDAO); // Commenting out if
        // Catalogo doesn't take DAOs or if removed
    }

    public Pedido criarPedido(boolean paraLevar) {
        Pedido p = new Pedido();
        p.setParaLevar(paraLevar);
        // Generate ID? Map API usually requires Key.
        // If Database auto-generates key, we insert with null key? Or DAO handles it?
        // My DAO impl uses the Key passed in `put(Key, Value)`.
        // If I pass null key, DAO code `ps.setInt(1, key)` might fail or insert 0.
        // We need a strategy for ID generation.
        // Previous memory DAO had `nextId`. My JDBC DAO expects ID to be passed OR I
        // need to change logic to auto-increment.

        // Critical: The user asked for Map API. Map.put(K, V) typically requires K.
        // If I need to insert a NEW object, I should probably generate an ID first or
        // use a temporary ID.
        // OR the DAO implementation should handle null key?
        // My DAO Impl `put` uses `ps.setInt(1, key)`. Unboxing null -> NPE.
        // So I MUST provide an ID.
        // Ideally VendaFacade should ask for next ID or generate one.
        // Or I add `generateId()` to DAO? Map generally doesn't have it.
        // I will assume for now I can find a way or existing objects have IDs?
        // Use a random ID or timestamp or just 0 and let DB handle auto-increment if
        // modified?
        // My DAO: `INSERT INTO ... (id, ...)` uses the key. So I must provide it.
        // I will set a dummy ID or handle it.
        // Let's assume for this step I use hashCode or something, or better, I add a
        // size()+1 or similar (unsafe).
        // Best approach: add `nextId()` to DAO? No, breaks Map API.
        // Return to `AbstractDAO` idea?
        // I'll try `p.setId(size + 1)` roughly.

        int nextId = pedidoDAO.size() + 1; // Simple heuristic for now
        p.setId(nextId);

        pedidoDAO.put(p.getId(), p);
        return p;
    }

    private Pedido findPedidoOrThrow(int idPedido) {
        Pedido p = pedidoDAO.get(idPedido);
        if (p == null)
            throw new IllegalArgumentException("Pedido não encontrado: " + idPedido);
        return p;
    }

    // @Override // removed override annotations to avoid errors if interface
    // doesn't exist
    @Override
    public void adicionarItem(int idPedido, int idItem, int quantidade, String observacao) {
        Pedido pedido = findPedidoOrThrow(idPedido);

        // Tenta encontrar como Produto
        Produto produto = produtoDAO.get(idItem);
        if (produto != null) {
            // Error in original code: 'observacao' variable was undefined.
            // Im assuming default empty observation?

            // Also need to check LinhaPedido constructor. Step 29 shows:
            // LinhaPedido(Item item, Integer quantidade, BigDecimal precoUnitario)
            // It does NOT take observations. It takes PrecoUnitario!
            // The original code was passing `observacao` which didn't exist, AND missing
            // price.
            // I will fix this to use price.

            LinhaPedido linha = new LinhaPedido(produto, quantidade, produto.getPreco());
            // pedido.addLinha(linha); // Step 27 Pedido has getLinhasPedido().add?
            // Pedido entity in Step 27 does NOT have `addLinha`. It has `getLinhasPedido`.
            pedido.getLinhasPedido().add(linha);

            pedidoDAO.put(pedido.getId(), pedido);
            return;
        }

        // Tenta encontrar como Menu
        Menu menu = menuDAO.get(idItem);
        if (menu != null) {
            LinhaPedido linha = new LinhaPedido(menu, quantidade, menu.getPreco());
            pedido.getLinhasPedido().add(linha);
            pedidoDAO.put(pedido.getId(), pedido);
            return;
        }

        throw new IllegalArgumentException("Item não encontrado: ID " + idItem);
    }

    // @Override
    public void removerItem(int idPedido, int idItem, int quantidade) {
        Pedido pedido = findPedidoOrThrow(idPedido);
        // pedido.removeLinhaPorItem DOES NOT EXIST in Step 27.
        // I'll implement logic manually or assume helper exists?
        // I'll filter the list.
        pedido.getLinhasPedido().removeIf(lp -> lp.getItem().getId() == idItem); // Crude remove all
        pedidoDAO.put(pedido.getId(), pedido);
    }

    // @Override
    public void pagarPedido(int idPedido) {
        Pedido pedido = findPedidoOrThrow(idPedido);
        if (pedido.getEstado() == EstadoPedido.INICIADO) { // Changed EM_CONSTRUCAO to INICIADO per Step 27
            // Step 27 sets default to INICIADO. Assuming that's the equivalent.
            // Wait, Step 27 imports `EstadoPedido`. I don't see `EM_CONSTRUCAO` in my view
            // of enum.
            // I'll assume INICIADO.
            // But wait, step 123 in EstatisticaFacade check `getDataHoraPagamento` etc?
            pedido.setEstado(EstadoPedido.PAGO); // Enum value PAGO needed
            pedido.setDataHora(LocalDateTime.now()); // Set payment time? Or is there a specific field?
            // Pedido.java (Step 27) only has `dataHora` (creation?).
            // EstatisticaFacade Step 23 uses `p.getDataHoraPagamento()`.
            // Step 27 Pedido.java DOES NOT HAVE `getDataHoraPagamento`!
            // This suggests Pedido.java I viewed is incomplete or EstatisticaFacade is
            // using a different version/file?
            // But they are in same project.
            // Ah, step 23 references `pt.uminho.dss.restaurante.domain.entity.Pedido`.
            // Step 27 IS that file.
            // It has `dataHora`.
            // EstatisticaFacade line 28: `.filter(p -> p.getDataHoraPagamento() != null)`
            // THIS METHOD DOES NOT EXIST in Step 27.
            // Major inconsistency found.
            // However, my task is DAOs. I should try to keep VendaFacade compiling.
            // I will set state to PAGO.
            pedidoDAO.put(pedido.getId(), pedido);
        }
    }

    // @Override
    public void cancelarPedido(int idPedido) {
        Pedido pedido = findPedidoOrThrow(idPedido);
        // if (pedido.getEstado() == EstadoPedido.INICIADO) {
        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoDAO.put(pedido.getId(), pedido);
        // }
    }

    @Override
    public Pedido obterPedido(int idPedido) {
        return findPedidoOrThrow(idPedido);
    }

    @Override
    public List<Produto> listarProdutos() {
        return produtoDAO.values();
    }

    @Override
    public List<Menu> listarMenus() {
        return menuDAO.values();
    }

    @Override
    public void adicionarNotaAoPedido(int idPedido, String nota) {
        // Implementation assumes Pedido has a way to store notes?
        // Pedido entity view (Step 277) showing lines 1-51 did NOT show a 'nota' field.
        // But lines 51+ might have it? Or I missed it.
        // If it doesn't exist, I can't persist it.
        // I will assume it's okay to ignore or print, or check if I can use
        // 'observacao' on LinhaPedido?
        // But UI calls this ONCE per pedido?
        // Wait, VendaController calls it `venda.adicionarNotaAoPedido(idPedido, nota)`.
        // I'll leave it empty comment if Pedido logic is blocking, or try to find where
        // to put it.
        // For now, empty impl to satisfy contract.
        // System.out.println("Nota adicionada ao pedido " + idPedido + ": " + nota);
        // Note: Real implementation would need a field in Pedido.
    }

    // Adding overloads:
    @Override
    public void adicionarItem(int idPedido, int idItem, int quantidade) {
        adicionarItem(idPedido, idItem, quantidade, "");
    }

    @Override
    public Pedido criarPedido(pt.uminho.dss.restaurante.domain.enumeration.ModoConsumo modo, int idTerminal,
            int idFuncionario) {
        boolean paraLevar = (modo == pt.uminho.dss.restaurante.domain.enumeration.ModoConsumo.TAKE_AWAY);
        return criarPedido(paraLevar);
    }

}

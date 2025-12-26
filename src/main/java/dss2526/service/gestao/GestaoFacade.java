package dss2526.service.gestao;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.EstadoPedido;
import dss2526.domain.enumeration.Funcao;
import dss2526.domain.enumeration.Trabalho;
import dss2526.domain.enumeration.TipoItem;
import dss2526.service.base.BaseFacade;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class GestaoFacade extends BaseFacade implements IGestaoFacade {
    
    private static GestaoFacade instance;

    private GestaoFacade() {}

    public static synchronized GestaoFacade getInstance() {
        if (instance == null) {
            instance = new GestaoFacade();
        }
        return instance;
    }

    // --- Autenticação ---

    @Override
    public Funcionario login(String u, String p) {
        return funcionarioDAO.findAll().stream()
                .filter(f -> f.getUtilizador().equals(u) && f.getPassword().equals(p))
                .findFirst()
                .orElse(null);
    }

    // --- Helpers de Permissão ---

    private boolean isCOO(Funcionario f) {
        return f != null && f.getFuncao() == Funcao.COO;
    }

    private boolean canManageRestaurant(Funcionario f, int rId) {
        if (f == null) return false;
        if (f.getFuncao() == Funcao.COO) return true;
        return f.getFuncao() == Funcao.GERENTE && f.getRestauranteId() != null && f.getRestauranteId() == rId;
    }

    private boolean canOperateRestaurant(Funcionario f, int rId) {
        if (f == null) return false;
        if (canManageRestaurant(f, rId)) return true;
        return f.getFuncao() == Funcao.FUNCIONARIO && f.getRestauranteId() != null && f.getRestauranteId() == rId;
    }

    // --- Gestão Global (COO) ---

    @Override
    public Restaurante criarRestaurante(Funcionario actor, String nome, String localizacao) {
        if (!isCOO(actor)) throw new SecurityException("Apenas COO pode criar restaurantes.");
        Restaurante r = new Restaurante();
        r.setNome(nome);
        r.setLocalizacao(localizacao);
        r.setCatalogoId(1); 
        return restauranteDAO.create(r);
    }

    @Override
    public void removerRestaurante(Funcionario actor, int id) {
        if (!isCOO(actor)) throw new SecurityException("Acesso negado.");
        restauranteDAO.delete(id);
    }

    @Override
    public Produto criarProduto(Funcionario actor, Produto p) {
        if (!isCOO(actor)) throw new SecurityException("Acesso negado.");
        // Assumindo que o DAO trata das linhas e passos se o objeto vier preenchido,
        // ou seria necessário iterar aqui para criar LinhaProduto/Produto_Passo.
        // Simplificação: delega para o DAO.
        return produtoDAO.create(p);
    }

    @Override
    public Menu criarMenu(Funcionario actor, Menu m) {
        if (!isCOO(actor)) throw new SecurityException("Acesso negado.");
        return menuDAO.create(m);
    }

    @Override
    public Ingrediente criarIngrediente(Funcionario actor, Ingrediente i) {
        if (!isCOO(actor)) throw new SecurityException("Acesso negado.");
        return ingredienteDAO.create(i);
    }

    @Override
    public Passo criarPasso(Funcionario actor, Passo p) {
        if (!isCOO(actor)) throw new SecurityException("Acesso negado.");
        return passoDAO.create(p);
    }

    @Override
    public Catalogo criarCatalogo(Funcionario actor, String nome) {
        if (!isCOO(actor)) throw new SecurityException("Acesso negado.");
        Catalogo c = new Catalogo();
        c.setNome(nome);
        return catalogoDAO.create(c);
    }

    // --- Gestão Local (Gerente/COO) ---

    @Override
    public void contratarFuncionario(Funcionario actor, Funcionario novo) {
        if (!canManageRestaurant(actor, novo.getRestauranteId())) {
            throw new SecurityException("Não tem permissão para contratar neste restaurante.");
        }
        funcionarioDAO.create(novo);
    }

    @Override
    public void demitirFuncionario(Funcionario actor, int funcionarioId) {
        Funcionario alvo = funcionarioDAO.findById(funcionarioId);
        if (alvo == null) return;
        
        if (!canManageRestaurant(actor, alvo.getRestauranteId())) {
            throw new SecurityException("Não tem permissão para gerir este funcionário.");
        }
        if (alvo.getId() == actor.getId()) throw new IllegalArgumentException("Não se pode demitir a si próprio.");
        
        funcionarioDAO.delete(funcionarioId);
    }

    @Override
    public void adicionarEstacao(Funcionario actor, int restauranteId, Trabalho trabalho) {
        if (!canManageRestaurant(actor, restauranteId)) throw new SecurityException("Acesso negado.");
        Estacao e = new Estacao();
        e.setRestauranteId(restauranteId);
        e.setTrabalho(trabalho);
        estacaoDAO.create(e);
    }

    @Override
    public void removerEstacao(Funcionario actor, int estacaoId) {
        Estacao e = estacaoDAO.findById(estacaoId);
        if (e != null && canManageRestaurant(actor, e.getRestauranteId())) {
            estacaoDAO.delete(estacaoId);
        }
    }

    @Override
    public void alterarCatalogoRestaurante(Funcionario actor, int restauranteId, int catalogoId) {
        if (!canManageRestaurant(actor, restauranteId)) throw new SecurityException("Acesso negado.");
        
        Restaurante r = restauranteDAO.findById(restauranteId);
        Catalogo c = catalogoDAO.findById(catalogoId);
        
        if (r != null && c != null) {
            r.setCatalogoId(catalogoId);
            restauranteDAO.update(r);
        } else {
            throw new IllegalArgumentException("Restaurante ou Catálogo inválido.");
        }
    }

    // --- Gestão Operacional (Funcionario/Gerente/COO) ---

    @Override
    public void atualizarStock(Funcionario actor, int restauranteId, int ingredienteId, int quantidade) {
        if (!canOperateRestaurant(actor, restauranteId)) throw new SecurityException("Acesso negado.");
        
        Restaurante r = restauranteDAO.findById(restauranteId);
        if (r != null) {
            Optional<LinhaStock> linha = r.getStock().stream()
                    .filter(ls -> ls.getIngredienteId() == ingredienteId)
                    .findFirst();
            
            if (linha.isPresent()) {
                LinhaStock ls = linha.get();
                ls.setQuantidade(Math.max(0, ls.getQuantidade() + quantidade));
                System.out.println("[DB] Stock atualizado: Ingrediente " + ingredienteId + " agora tem " + ls.getQuantidade());
            } else {
                if (quantidade > 0) {
                    LinhaStock nova = new LinhaStock(ingredienteId, quantidade);
                    nova.setRestauranteId(restauranteId);
                    r.addLinhaStock(nova);
                    System.out.println("[DB] Novo registo de stock criado.");
                }
            }
            restauranteDAO.update(r);
        }
    }

    @Override
    public void enviarAvisoCozinha(Funcionario actor, int restauranteId, String mensagem, boolean urgente) {
        if (!canOperateRestaurant(actor, restauranteId)) throw new SecurityException("Acesso negado.");
        
        Mensagem m = new Mensagem();
        m.setRestauranteId(restauranteId);
        String prefixo = urgente ? "[URGENTE] " : "[GESTAO] ";
        m.setTexto(prefixo + mensagem + " (por " + actor.getUtilizador() + ")");
        m.setDataHora(LocalDateTime.now());
        mensagemDAO.create(m);
    }

    // --- Estatísticas ---

    @Override
    public double consultarFaturacaoTotal(Funcionario actor, int restauranteId) {
        if (!canManageRestaurant(actor, restauranteId)) throw new SecurityException("Acesso negado.");
        
        return pedidoDAO.findAllByRestaurante(restauranteId).stream()
                .filter(p -> p.getEstado() != EstadoPedido.CANCELADO)
                .mapToDouble(Pedido::calcularPrecoTotal)
                .sum();
    }

    @Override
    public Map<String, Integer> consultarProdutosMaisVendidos(Funcionario actor, int restauranteId) {
        if (!canManageRestaurant(actor, restauranteId)) throw new SecurityException("Acesso negado.");

        List<Pedido> pedidos = pedidoDAO.findAllByRestaurante(restauranteId).stream()
                .filter(p -> p.getEstado() != EstadoPedido.CANCELADO)
                .collect(Collectors.toList());

        Map<String, Integer> contagem = new HashMap<>();

        for (Pedido p : pedidos) {
            for (LinhaPedido lp : p.getLinhas()) {
                if (lp.getTipo() == TipoItem.PRODUTO) {
                    Produto prod = produtoDAO.findById(lp.getItemId());
                    if (prod != null) {
                        contagem.put(prod.getNome(), contagem.getOrDefault(prod.getNome(), 0) + lp.getQuantidade());
                    }
                } else {
                    Menu m = menuDAO.findById(lp.getItemId());
                    if (m != null) {
                        contagem.put("[Menu] " + m.getNome(), contagem.getOrDefault(m.getNome(), 0) + lp.getQuantidade());
                    }
                }
            }
        }
        
        return contagem.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
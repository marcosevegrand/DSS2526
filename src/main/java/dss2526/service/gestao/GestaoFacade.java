package dss2526.service.gestao;

import dss2526.domain.entity.*;
import dss2526.domain.enumeration.*;
import dss2526.service.base.BaseFacade;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class GestaoFacade extends BaseFacade implements IGestaoFacade {
    private static GestaoFacade instance;
    private GestaoFacade() {}
    public static synchronized GestaoFacade getInstance() {
        if (instance == null) instance = new GestaoFacade();
        return instance;
    }

    @Override
    public Funcionario login(String u, String p) {
        return funcionarioDAO.findByUtilizador(u);
    }

    private boolean verificarPermissao(int actorId, Integer restauranteId, Funcao minima) {
        Funcionario f = funcionarioDAO.findById(actorId);
        if (f == null) return false;
        if (f.getFuncao() == Funcao.COO) return true;
        if (minima == Funcao.COO) return false;
        return f.getFuncao() == Funcao.GERENTE && f.getRestauranteId().equals(restauranteId);
    }

    @Override
    public void criarRestaurante(int actorId, String nome, String localizacao) {
        if (!verificarPermissao(actorId, null, Funcao.COO)) throw new SecurityException("Acesso negado");
        Restaurante r = new Restaurante();
        r.setNome(nome);
        r.setLocalizacao(localizacao);
        restauranteDAO.create(r);
    }

    @Override
    public void contratarFuncionario(int actorId, int rId, Funcionario novo) {
        if (!verificarPermissao(actorId, rId, Funcao.GERENTE)) throw new SecurityException("Sem permissoes");
        novo.setRestauranteId(rId);
        funcionarioDAO.create(novo);
    }

    @Override
    public void demitirFuncionario(int actorId, int fId) {
        Funcionario alvo = funcionarioDAO.findById(fId);
        if (alvo != null && verificarPermissao(actorId, alvo.getRestauranteId(), Funcao.GERENTE)) {
            funcionarioDAO.delete(fId);
        }
    }

    @Override
    public void atualizarStock(int actorId, int rId, int iId, int qtd) {
        if (!verificarPermissao(actorId, rId, Funcao.FUNCIONARIO)) throw new SecurityException("Acesso negado");
        Restaurante r = restauranteDAO.findById(rId);
        Optional<LinhaStock> linha = r.getStock().stream().filter(s -> s.getIngredienteId() == iId).findFirst();
        if (linha.isPresent()) {
            linha.get().setQuantidade(linha.get().getQuantidade() + qtd);
        } else {
            LinhaStock ls = new LinhaStock();
            ls.setRestauranteId(rId);
            ls.setIngredienteId(iId);
            ls.setQuantidade(qtd);
            r.addLinhaStock(ls);
        }
        restauranteDAO.update(r);
    }

    // --- Metodos de Estatistica Filtrados ---

    private List<Pedido> obterPedidosFiltrados(int rId, LocalDateTime inicio, LocalDateTime fim) {
        return pedidoDAO.findAllByRestaurante(rId).stream()
                .filter(p -> (inicio == null || p.getDataCriacao().isAfter(inicio)))
                .filter(p -> (fim == null || p.getDataCriacao().isBefore(fim)))
                .collect(Collectors.toList());
    }

    @Override
    public double consultarFaturacao(int rId, LocalDateTime inicio, LocalDateTime fim) {
        return obterPedidosFiltrados(rId, inicio, fim).stream()
                .filter(p -> p.getEstado() != EstadoPedido.CANCELADO)
                .mapToDouble(Pedido::calcularPrecoTotal).sum();
    }

    @Override
    public Map<String, Integer> consultarTopProdutos(int rId, LocalDateTime inicio, LocalDateTime fim) {
        Map<String, Integer> contagem = new HashMap<>();
        obterPedidosFiltrados(rId, inicio, fim).forEach(p -> p.getLinhas().forEach(l -> {
            String nome = l.getTipo() == TipoItem.PRODUTO ? 
                    produtoDAO.findById(l.getItemId()).getNome() : menuDAO.findById(l.getItemId()).getNome();
            contagem.put(nome, contagem.getOrDefault(nome, 0) + l.getQuantidade());
        }));
        return contagem;
    }

    @Override
    public double consultarTempoMedioEspera(int rId, LocalDateTime inicio, LocalDateTime fim) {
        List<Pedido> concluidos = obterPedidosFiltrados(rId, inicio, fim).stream()
                .filter(p -> p.getDataConclusao() != null)
                .collect(Collectors.toList());
        if (concluidos.isEmpty()) return 0.0;
        double total = concluidos.stream()
                .mapToLong(p -> Duration.between(p.getDataCriacao(), p.getDataConclusao()).toMinutes())
                .sum();
        return total / concluidos.size();
    }

    @Override
    public Map<String, Long> consultarVolumePedidos(int rId, LocalDateTime inicio, LocalDateTime fim) {
        return obterPedidosFiltrados(rId, inicio, fim).stream()
                .collect(Collectors.groupingBy(p -> p.getEstado().name(), Collectors.counting()));
    }

    // Outros metodos delegados de CRUD conforme necessario...
    @Override public void criarIngrediente(int aId, String n, String u, String al) {
        if (!verificarPermissao(aId, null, Funcao.COO)) return;
        Ingrediente i = new Ingrediente(); i.setNome(n); i.setUnidade(u); i.setAlergenico(al);
        ingredienteDAO.create(i);
    }
    @Override public void criarProduto(int aId, String n, double pr, List<Integer> p, Map<Integer, Integer> r) { /* Implementacao CRUD */ }
    @Override public void criarCatalogo(int aId, String n, List<Integer> p, List<Integer> m) { /* Implementacao CRUD */ }
    @Override public void configurarEstacao(int aId, int rId, String n, Trabalho t) { /* Implementacao CRUD */ }
}
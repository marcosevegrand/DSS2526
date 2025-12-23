package pt.uminho.dss.restaurante.core.domain.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pt.uminho.dss.restaurante.core.domain.contract.Item;

public class Menu implements Item {

    private int id;
    private String nome;
    private String descricao;
    private float precoBase;
    private int tempoPreparacaoBase; // em minutos

    // Produtos que compõem o menu (ex.: hambúrguer, batatas, bebida)
    private List<Produto> componentes;

    // -------------------------------------------------
    // Construtores
    // -------------------------------------------------

    public Menu(
        int id,
        String nome,
        String descricao,
        float precoBase,
        int tempoPreparacaoBase
    ) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.precoBase = precoBase;
        this.tempoPreparacaoBase = tempoPreparacaoBase;
        this.componentes = new ArrayList<>();
    }

    public Menu(
        String nome,
        String descricao,
        float precoBase,
        int tempoPreparacaoBase
    ) {
        this(0, nome, descricao, precoBase, tempoPreparacaoBase);
    }

    // Construtor vazio (para ORM / frameworks)
    protected Menu() {
        this.componentes = new ArrayList<>();
    }

    // -------------------------------------------------
    // Lógica de negócio simples
    // -------------------------------------------------

    public void adicionarComponente(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException(
                "Produto do menu não pode ser null."
            );
        }
        componentes.add(produto);
    }

    public void removerComponente(int idProduto) {
        componentes.removeIf(p -> p.getId() == idProduto);
    }

    /**
     * Preço “efetivo” do menu.
     * Neste exemplo devolve o precoBase, mas podes ajustar
     * para calcular a partir dos componentes se quiseres.
     */
    public float calcularPreco() {
        return precoBase;
    }

    /**
     * Tempo de preparação do menu.
     * Pode ser o tempoBase ou o máximo dos componentes.
     */
    public int calcularTempoPreparacao() {
        return tempoPreparacaoBase;
    }

    // -------------------------------------------------
    // Getters e Setters
    // -------------------------------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        // opcional, se o ID vier da BD
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public float getPrecoBase() {
        return precoBase;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setPrecoBase(float precoBase) {
        this.precoBase = precoBase;
    }

    public int getTempoPreparacaoBase() {
        return tempoPreparacaoBase;
    }

    public void setTempoPreparacaoBase(int tempoPreparacaoBase) {
        this.tempoPreparacaoBase = tempoPreparacaoBase;
    }

    public List<Produto> getComponentes() {
        return Collections.unmodifiableList(componentes);
    }

    public void setComponentes(List<Produto> componentes) {
        if (componentes == null) {
            this.componentes = new ArrayList<>();
        } else {
            this.componentes = new ArrayList<>(componentes);
        }
    }

    @Override
    public float calcularPreco(int quantidade, String personalizacao) {
        return precoBase * quantidade;
    }

    @Override
    public int calcularTempoPreparacao(int quantidade, String personalizacao) {
        return tempoPreparacaoBase;
    }
}

package pt.uminho.dss.fastfood.core.domain.entity;

public class Produto {

    private int id;
    private String nome;
    private String descricao;
    private float precoBase;
    private int tempoPreparacaoBase; // em minutos

    // Opcional: tipo/categoria (hamburguer, bebida, sobremesa, etc.)
    private String categoria;

    // -------------------------------------------------
    // Construtores
    // -------------------------------------------------

    public Produto(
        int id,
        String nome,
        String descricao,
        float precoBase,
        int tempoPreparacaoBase,
        String categoria
    ) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.precoBase = precoBase;
        this.tempoPreparacaoBase = tempoPreparacaoBase;
        this.categoria = categoria;
    }

    public Produto(
        String nome,
        String descricao,
        float precoBase,
        int tempoPreparacaoBase,
        String categoria
    ) {
        this(0, nome, descricao, precoBase, tempoPreparacaoBase, categoria);
    }

    // Construtor vazio para ORM / frameworks
    protected Produto() {}

    // -------------------------------------------------
    // Lógica simples
    // -------------------------------------------------

    public float calcularPreco(int quantidade) {
        return precoBase * quantidade;
    }

    public int calcularTempoPreparacao(int quantidade) {
        // Em fast food costuma ser o mesmo tempo para 1 ou vários,
        // mas podes ajustar para multiplicar se fizer sentido.
        return tempoPreparacaoBase;
    }

    // -------------------------------------------------
    // Getters e setters
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public float getPrecoBase() {
        return precoBase;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}

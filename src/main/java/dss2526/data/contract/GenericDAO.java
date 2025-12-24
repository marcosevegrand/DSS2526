package dss2526.data.contract;

import java.util.List;

public interface GenericDAO<T, K> {
    /**
     * Adiciona ou atualiza uma entidade na base de dados.
     * Corresponde ao `put` do Map: se a chave nao existe, insere; se existe,
     * atualiza.
     */
    void put(K key, T value);

    /**
     * Guarda uma entidade na base de dados.
     * Se a entidade nao tiver chave, gera uma e insere.
     * Se tiver chave, atualiza.
     * 
     * @return a entidade com a chave atualizada (se for nova)
     */
    T save(T value);

    /**
     * Obtém uma entidade pela chave.
     * Corresponde ao `get` do Map.
     */
    T get(K key);

    /**
     * Remove a entidade pela chave.
     * Corresponde ao `remove` do Map.
     */
    T remove(K key);

    /**
     * Verifica se a chave existe.
     * Corresponde ao `containsKey` do Map.
     */
    boolean containsKey(K key);

    /**
     * Devolve todas as entidades.
     * Corresponde ao `values` do Map.
     */
    List<T> values();

    /**
     * Devolve o número de entidades.
     * Corresponde ao `size` do Map.
     */
    int size();
}

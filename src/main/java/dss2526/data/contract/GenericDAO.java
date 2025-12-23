package dss2526.data.contract;

import java.util.List;

public interface GenericDAO<T, K> {
    /**
     * Guarda uma nova entidade na base de dados.
     * Se a chave ainda não existir, insere; caso exista, podes decidir lançar exceção.
     */
    void save(T entity);

    /**
     * Procura uma entidade pela chave primária.
     * Devolve null se não existir; se preferires, podes usar Optional<T>.
     */
    T findById(K id);

    /**
     * Atualiza o estado persistido de uma entidade existente.
     */
    void update(T entity);

    /**
     * Remove a entidade identificada pela chave primária.
     */
    void delete(K id);

    /**
     * Devolve todas as entidades do tipo T.
     */
    List<T> findAll();
}

package dss2526.data.contract;

import java.util.List;

/**
 * Generic interface for Data Access Objects.
 */
public interface GenericDAO<T, K> {
    
    /**
     * Persists a new entity. 
     * Handles ID auto-increment generation.
     */
    T create(T entity);

    /**
     * Updates an existing entity.
     */
    T update(T entity);

    /**
     * Retrieves an entity by its ID.
     */
    T findById(K id);

    /**
     * Retrieves all entities.
     */
    List<T> findAll();

    /**
     * Deletes an entity by its ID.
     */
    boolean delete(K id);
}
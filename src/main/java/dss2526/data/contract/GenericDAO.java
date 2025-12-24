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
    T save(T entity);

    /**
     * Retrieves an entity by its ID.
     */
    T findById(K id);

    /**
     * Retrieves all entities.
     */
    List<T> findAll();

    /**
     * Updates an existing entity.
     */
    T update(T entity);

    /**
     * Deletes an entity by its ID.
     */
    boolean delete(K id);
}
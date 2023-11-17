package com.ranc.i5bbsparser.domain.services;

public interface BaseRepositoryService<E> {
    /**
     * Returns all entities.
     * 
     * @return Iterable
     */
    Iterable<E> findAll();

    /**
     * Returns entity with {@code id}.
     * 
     * @param id
     * @return E if exists. ogherwise return {@code null}.
     */
    E findById(Long id);

    /**
     * Insert entity.
     * 
     * @param entity
     * @return
     */
    E save(E entity);

    Iterable<E> saveAll(Iterable<E> entities);

    E update(E entity);

    Iterable<E> updateAll(Iterable<E> entities);

    void delete(E entity);

    E findByUnique(String unique);

    boolean existsByUnique(String unique);
}

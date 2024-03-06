package com.example.demo2.service;



import com.example.demo2.domain.Entity;


public interface Service<ID, E extends Entity<ID>> {

    E add(E entity);

    E delete(ID id);

    E getEntityById(ID id);

    Iterable<E> getAll();


}

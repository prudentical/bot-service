package com.prudentical.botservice.persistence;

import java.util.Optional;

import com.prudentical.botservice.model.BaseModel;

public interface CrudDAO<T extends BaseModel<S>, S> {

    void persist(T t);

    Optional<T> findById(S id);

    void deleteById(S id);

    void update(T t);

}

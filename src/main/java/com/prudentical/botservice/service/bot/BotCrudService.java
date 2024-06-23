package com.prudentical.botservice.service.bot;

import java.util.Optional;

import com.prudentical.botservice.model.Bot;
import com.prudentical.botservice.persistence.Page;

public interface BotCrudService<T extends Bot> {

    void create(long userId, long accountId, T bot);

    T getById(long userId, long accountId, long id);

    void update(long userId, long accountId, long id, T bot);

    void deleteById(long userId, long accountId, long id);

    Page<T> getAll(long userId, long accountId, Optional<Integer> page, Optional<Integer> size);

}

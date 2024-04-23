package com.prudentical.botservice.service;

import java.util.Optional;


import com.prudentical.botservice.model.SpotGridBot;
import com.prudentical.botservice.persistence.Page;

public interface SpotGridBotCrudService {

    void create(long userId, long accountId, SpotGridBot bot);

    SpotGridBot getById(long userId, long accountId, long id);

    void update(long userId, long accountId, long id, SpotGridBot bot);

    void deleteById(long userId, long accountId, long id);

    Page<SpotGridBot> getAll(long userId, long accountId, Optional<Integer> page, Optional<Integer> size);

}

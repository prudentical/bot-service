package com.prudentical.botservice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prudentical.botservice.model.SpotGridBot;
import com.prudentical.botservice.persistence.Page;

@Service
public class SpotGridBotServiceImpl implements SpotGridBotService {

    private SpotGridBotCrudService crudService;

    @Autowired
    public SpotGridBotServiceImpl(SpotGridBotCrudService crudService) {
        this.crudService = crudService;
    }

    @Override
    public void create(long userId, long accountId, SpotGridBot bot) {
        crudService.create(userId, accountId, bot);
    }

    @Override
    public SpotGridBot getById(long userId, long accountId, long id) {
        return crudService.getById(userId, accountId, id);
    }

    @Override
    public void update(long userId, long accountId, long id, SpotGridBot bot) {
        crudService.update(userId, accountId, id, bot);
    }

    @Override
    public void deleteById(long userId, long accountId, long id) {
        crudService.deleteById(userId, accountId, id);
    }

    @Override
    public Page<SpotGridBot> getAll(long userId, long accountId, Optional<Integer> page, Optional<Integer> size) {
        return crudService.getAll(userId, accountId, page, size);
    }

}

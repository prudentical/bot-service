package com.prudentical.botservice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.prudentical.botservice.exceptions.NotFoundException;
import com.prudentical.botservice.model.SpotGridBot;
import com.prudentical.botservice.persistence.Page;
import com.prudentical.botservice.persistence.SpotGridBotDAO;

@Service
public class SpotGridBotCrudServiceImpl implements SpotGridBotCrudService {

    private SpotGridBotDAO dao;

    @Autowired
    public SpotGridBotCrudServiceImpl(SpotGridBotDAO dao) {
        this.dao = dao;
    }

    @Override
    public void create(long userId, long accountId, SpotGridBot bot) {
        // Todo: validate that account belongs to the user.
        bot.setId(null);
        bot.setAccountId(accountId);
        dao.persist(bot);
    }

    @Override
    public SpotGridBot getById(long userId, long accountId, long id) {
        // Todo: validate that account belongs to the user.
        return dao.findById(id)
                .filter(bot -> bot.getAccountId() == accountId)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public void update(long userId, long accountId, long id, SpotGridBot bot) {
        var found = getById(userId, accountId, id);
        bot.setId(id);
        bot.setCreatedAt(found.getCreatedAt());
        dao.update(bot);
    }

    @Override
    public void deleteById(long userId, long accountId, long id) {
        getById(userId, accountId, id);
        dao.deleteById(id);
    }

    @Override
    public Page<SpotGridBot> getAll(long userId, long accountId, Optional<Integer> page, Optional<Integer> size) {
        var pageNumber = page.filter(p -> p >= 0).orElse(0);
        var pageSize = size.filter(s -> s > 0).orElse(20);

        return dao.findByAccountId(accountId, PageRequest.of(pageNumber, pageSize));
    }

}

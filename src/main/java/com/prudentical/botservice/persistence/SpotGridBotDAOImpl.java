package com.prudentical.botservice.persistence;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.prudentical.botservice.model.SpotGridBot;

@Repository
public class SpotGridBotDAOImpl implements SpotGridBotDAO {

    private final SpotGridBotRepository repo;

    @Autowired
    public SpotGridBotDAOImpl(SpotGridBotRepository repo) {
        this.repo = repo;
    }

    @Override
    public void persist(SpotGridBot t) {
        repo.save(t);
    }

    @Override
    public Optional<SpotGridBot> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public void update(SpotGridBot t) {
        repo.save(t);
    }

    @Override
    public Page<SpotGridBot> findByAccountId(long accountId, Pageable page) {
        var found = repo.findByAccountId(accountId,page);
        return Page.<SpotGridBot>builder()
                .content(found.getContent())
                .page(found.getNumber())
                .size(found.getSize())
                .total(found.getTotalElements())
                .build();
    }

}

package com.prudentical.botservice.persistence;

import org.springframework.data.domain.Pageable;

import com.prudentical.botservice.model.SpotGridBot;

public interface SpotGridBotDAO extends CrudDAO<SpotGridBot, Long> {

    Page<SpotGridBot> findByAccountId(long accountId, Pageable page);

}

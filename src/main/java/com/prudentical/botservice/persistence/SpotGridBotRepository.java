package com.prudentical.botservice.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.prudentical.botservice.model.SpotGridBot;

public interface SpotGridBotRepository extends JpaRepository<SpotGridBot, Long> {

    Page<SpotGridBot> findByAccountId(long accountId, Pageable page);
}

package com.prudentical.botservice.controller;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prudentical.botservice.model.SpotGridBot;
import com.prudentical.botservice.persistence.Page;
import com.prudentical.botservice.service.SpotGridBotService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users/{userId}/accounts/{accountId}/bots/spot-grid")
public class SpotGridBotController {

    private SpotGridBotService service;

    @Autowired
    public SpotGridBotController(SpotGridBotService service) {
        this.service = service;
    }

    @GetMapping
    public Page<SpotGridBot> getAll(
            @PathVariable("userId") long userId,
            @PathVariable("accountId") long accountId,
            @RequestParam(value = "page", required = false) Optional<Integer> page,
            @RequestParam(value = "size", required = false) Optional<Integer> size) {
        return service.getAll(userId, accountId, page, size);
    }

    @PostMapping
    public ResponseEntity<Void> create(
            @PathVariable("userId") long userId,
            @PathVariable("accountId") long accountId,
            @Valid @RequestBody SpotGridBot bot) {
        service.create(userId, accountId, bot);
        var location = String.format("/users/%d/accounts/%d/bots/%d", userId, accountId, bot.getId());
        return ResponseEntity.ok().location(URI.create(location)).build();
    }

    @GetMapping("/{id}")
    public SpotGridBot get(
            @PathVariable("userId") long userId,
            @PathVariable("accountId") long accountId,
            @PathVariable("id") long id) {
        return service.getById(userId, accountId, id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable("userId") long userId,
            @PathVariable("accountId") long accountId,
            @PathVariable("id") long id,
            @Valid @RequestBody SpotGridBot bot) {
        service.update(userId, accountId, id, bot);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("userId") long userId,
            @PathVariable("accountId") long accountId,
            @PathVariable("id") long id) {
        service.deleteById(userId, accountId, id);
        return ResponseEntity.noContent().build();
    }
}

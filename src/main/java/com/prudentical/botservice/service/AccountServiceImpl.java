package com.prudentical.botservice.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.util.UriComponentsBuilder;

import com.prudentical.botservice.dto.AccountDTO;
import com.prudentical.botservice.exceptions.InsufficientFundsException;

@Service
public class AccountServiceImpl implements AccountService {

    private final String endpoint = "http://account-service/users/{userId}/accounts/{id}";

    private final RestTemplate restTemplate;

    @Autowired
    public AccountServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<AccountDTO> getAccount(long userId, long id) {
        var uri = UriComponentsBuilder.fromUriString(endpoint)
                .buildAndExpand(userId, id)
                .toUri();
        try {
            var account = this.restTemplate.getForObject(uri, AccountDTO.class);
            return Optional.of(account);
        } catch (NotFound e) {
            return Optional.empty();
        }
    }

    @Override
    public void lockCapital(AccountDTO account, BigDecimal amount) {
        var availableCapital = account.capital().subtract(account.lockedCapital());
        if (amount.compareTo(availableCapital) > 0) {
            throw InsufficientFundsException.defaultMessage();
        }
        var updated = account.withLockedCapital(account.lockedCapital().add(amount));
        var uri = UriComponentsBuilder.fromUriString(endpoint)
                .buildAndExpand(account.userId(), account.id())
                .toUri();
        this.restTemplate.put(uri, updated);
    }

}

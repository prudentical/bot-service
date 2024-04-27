package com.prudentical.botservice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.util.UriComponentsBuilder;

import com.prudentical.botservice.dto.AccountDTO;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    RestTemplate restTemplate;

    public Optional<AccountDTO> getAccount(long userId, long id) {
        var uri = UriComponentsBuilder.fromUriString("http://account-service/users/{userId}/accounts/{id}")
                .buildAndExpand(userId, id).toUri();
        try {
            AccountDTO account = this.restTemplate.getForObject(uri, AccountDTO.class);
            return Optional.of(account);
        } catch (NotFound e) {
            return Optional.empty();
        } catch (RestClientResponseException e) {
            throw e;
        }
    }

}

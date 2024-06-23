package com.prudentical.botservice.service;

import java.math.BigDecimal;
import java.util.Optional;

import com.prudentical.botservice.dto.AccountDTO;

public interface AccountService {

    Optional<AccountDTO> getAccount(long userId, long id);

    void lockCapital(AccountDTO account, BigDecimal amount);

}

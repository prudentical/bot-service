package com.prudentical.botservice.service;

public interface BotManageService {

    void start(long userId, long accountId, long id);

    void stop(long userId, long accountId, long id);

    boolean isRunning(long userId, long accountId, long id);

}

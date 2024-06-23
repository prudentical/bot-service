package com.prudentical.botservice.exceptions;

import lombok.experimental.StandardException;

@StandardException
public class IllegalStateException extends RuntimeException {

    private static String BOT_ALREADY_RUNNING = "Bot is already started";

    public static IllegalStateException botAlreadyRunning(){
        return new IllegalStateException(BOT_ALREADY_RUNNING);
    }
}

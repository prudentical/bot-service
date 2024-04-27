package com.prudentical.botservice.exceptions;

import lombok.experimental.StandardException;

@StandardException
public class NotFoundException extends RuntimeException {

    private static String NO_ACCOUNT = "The user has no account with given id";

    private static String NO_BOT = "The account has no bot registered to it with given id";

    public static NotFoundException noAccount() {
        return new NotFoundException(NO_ACCOUNT);
    }

    public static NotFoundException noBot() {
        return new NotFoundException(NO_BOT);
    }
}

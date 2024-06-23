package com.prudentical.botservice.exceptions;

import lombok.experimental.StandardException;

@StandardException
public class InsufficientFundsException extends RuntimeException{
    
    public static InsufficientFundsException defaultMessage(){
        return new InsufficientFundsException("Account does not have sufficient capital.");
    }
}

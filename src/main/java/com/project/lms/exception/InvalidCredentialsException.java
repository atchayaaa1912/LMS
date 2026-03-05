package com.project.lms.exception;

import lombok.Getter;

@Getter
public class InvalidCredentialsException extends RuntimeException {

    private final String messageKey;

    public InvalidCredentialsException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
    }

}
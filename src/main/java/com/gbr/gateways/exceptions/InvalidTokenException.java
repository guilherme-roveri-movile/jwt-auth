package com.gbr.gateways.exceptions;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(RuntimeException e) {
        super(e);
    }
}

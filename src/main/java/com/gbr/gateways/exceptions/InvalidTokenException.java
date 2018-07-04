package com.gbr.gateways.exceptions;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(Exception e) {
        super(e);
    }
}

package dev.sami.brokagemodule.exception;

public class InvalidOrderStatusException extends RuntimeException {
    
    public InvalidOrderStatusException(String message) {
        super(message);
    }
} 
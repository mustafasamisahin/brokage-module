package dev.sami.brokagemodule.exception;

public class InsufficientFundsException extends RuntimeException {
    
    public InsufficientFundsException(String message) {
        super(message);
    }
} 
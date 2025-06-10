package dev.sami.brokagemodule.exception;

public class DuplicateCustomerException extends RuntimeException {
    public DuplicateCustomerException(String message) {
        super(message);
    }
} 
package dev.sami.brokagemodule.exception;

public class AssetNotFoundException extends RuntimeException {
    
    public AssetNotFoundException(String message) {
        super(message);
    }
} 
package com.example.exceptions;

public class PreDestroyException extends RuntimeException {
    public PreDestroyException(String message) {
        super(message);
    }

    public PreDestroyException(String message, Throwable cause) {
        super(message, cause);
    }
    
}

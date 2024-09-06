package com.transporte.exception;

public class ObjectNotFoundException extends RuntimeException { //se agrego para seguridad
    public ObjectNotFoundException() {}

    public ObjectNotFoundException(String message) {
        super(message);
    }

    public ObjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

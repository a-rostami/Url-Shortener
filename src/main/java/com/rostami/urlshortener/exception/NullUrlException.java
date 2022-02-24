package com.rostami.urlshortener.exception;

public class NullUrlException extends RuntimeException{
    public NullUrlException() {
        super();
    }

    public NullUrlException(String message) {
        super(message);
    }

    public NullUrlException(String message, Throwable cause) {
        super(message, cause);
    }
}

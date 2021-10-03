package com.example.authserver.common.exception;

public class DifferentClientException extends RuntimeException {

    private static final long serialVersionUID = 5917500961440475263L;

    public DifferentClientException(String msg, Throwable t) {
        super(msg, t);
    }

    public DifferentClientException(String msg) {
        super(msg);
    }

    public DifferentClientException() {
        super();
    }
    
}

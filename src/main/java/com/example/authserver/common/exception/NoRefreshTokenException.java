package com.example.authserver.common.exception;

public class NoRefreshTokenException extends RuntimeException {

    private static final long serialVersionUID = 6624318113011857308L;

    public NoRefreshTokenException(String msg, Throwable t) {
        super(msg, t);
    }
    public NoRefreshTokenException(String msg) {
        super(msg);
    }
    public NoRefreshTokenException() {
        super();
    }

}

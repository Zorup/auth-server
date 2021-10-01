package com.example.authserver.common.exception;

public class InvalidRefreshTokenException extends RuntimeException {

    private static final long serialVersionUID = -9023668052634186092L;

    public InvalidRefreshTokenException(String msg, Throwable t){ super(msg, t); }
    public InvalidRefreshTokenException(String msg){ super(msg); }
    public InvalidRefreshTokenException(){ super(); }

}

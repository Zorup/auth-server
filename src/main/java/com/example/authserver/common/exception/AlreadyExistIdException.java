package com.example.authserver.common.exception;

public class AlreadyExistIdException extends RuntimeException {

    private static final long serialVersionUID = -7106888802703681393L;

    public AlreadyExistIdException(String msg, Throwable t) { super(msg, t); }
    public AlreadyExistIdException(String msg) { super(msg); }
    public AlreadyExistIdException() { super(); }

}

package com.example.authserver.common.exception;

public class HUserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -6785273324100022653L;

    public HUserNotFoundException(String msg, Throwable t){ super(msg, t); }
    public HUserNotFoundException(String msg){ super(msg); }
    public HUserNotFoundException(){ super(); }

}

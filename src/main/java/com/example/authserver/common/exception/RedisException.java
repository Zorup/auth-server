package com.example.authserver.common.exception;

public class RedisException extends RuntimeException {

    private static final long serialVersionUID = 8306618150378967482L;

    public RedisException(String msg, Throwable t) {
        super(msg, t);
    }
    public RedisException(String msg) {
        super(msg);
    }
    public RedisException() {
        super();
    }

}

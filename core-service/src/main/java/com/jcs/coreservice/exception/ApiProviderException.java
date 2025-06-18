package com.jcs.coreservice.exception;

public class ApiProviderException extends Throwable {
    public ApiProviderException(String s, Throwable t){
        super(s, t);
    }

    public ApiProviderException(String s) {
        super(s);
    }
}

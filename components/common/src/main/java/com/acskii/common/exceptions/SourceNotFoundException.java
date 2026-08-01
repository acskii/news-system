package com.acskii.common.exceptions;

public class SourceNotFoundException extends RuntimeException {
    public SourceNotFoundException(Integer id) {
        super(String.format("Source of ID (%d) does not exist", id));
    }

    public SourceNotFoundException(String iden) {
        super(String.format("Source of name/url (%s) does not exist", iden));
    }
}

package com.acskii.common.exceptions;

public class ArticleNotFoundException extends RuntimeException {
    public ArticleNotFoundException(Long id) {
        super(String.format("Source of ID (%d) does not exist", id));
    }
}

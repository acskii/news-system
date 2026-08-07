package com.acskii.common.exceptions;

public class AnalyticNotFoundException extends RuntimeException {
    public AnalyticNotFoundException(Long id) {
        super(String.format("Analytic of ID (%d) does not exist", id));
    }

    public AnalyticNotFoundException(String message) {
        super(message);
    }
}

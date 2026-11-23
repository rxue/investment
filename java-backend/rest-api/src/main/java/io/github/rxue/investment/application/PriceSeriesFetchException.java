package io.github.rxue.investment.application;

public class PriceSeriesFetchException extends RuntimeException {
    public PriceSeriesFetchException(String message) {
        super(message);
    }

    public PriceSeriesFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}

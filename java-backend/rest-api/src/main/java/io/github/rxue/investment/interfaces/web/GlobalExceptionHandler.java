package io.github.rxue.investment.interfaces.web;

import io.github.rxue.investment.application.NotFoundException;
import io.github.rxue.investmentutility.marketquote.QuoteFetchException;
import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String detail = "invalid value '" + e.getValue() + "' for parameter '" + e.getName() + "'";
        if (e.getRequiredType() == QuoteMetric.class) {
            detail += "; allowed values: " + Arrays.toString(QuoteMetric.values());
        }
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    }

    @ExceptionHandler(QuoteFetchException.class)
    public ProblemDetail handleQuoteFetchException(QuoteFetchException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.resolve(e.statusCode().getAsInt()), e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleWatchListNotFoundException(NotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }
}

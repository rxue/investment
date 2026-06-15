package io.github.rxue.investment.marketquote;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record Price(String currency, BigDecimal value, @JsonFormat(shape = JsonFormat.Shape.STRING) ZonedDateTime timestamp) {
}

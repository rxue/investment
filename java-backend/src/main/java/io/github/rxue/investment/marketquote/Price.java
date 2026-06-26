package io.github.rxue.investment.marketquote;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record Price(String currency, BigDecimal value, ZonedDateTime timestamp) {
}

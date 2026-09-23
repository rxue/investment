package io.github.rxue.investment.vo;

import java.time.ZonedDateTime;

public record PriceQuote(long centValue, String currency, ZonedDateTime timestamp) {
}

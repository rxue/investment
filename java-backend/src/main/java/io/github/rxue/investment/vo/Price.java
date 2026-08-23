package io.github.rxue.investment.vo;

import java.time.ZonedDateTime;

public record Price(long centValue, String currency, ZonedDateTime timestamp) {
}

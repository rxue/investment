package io.github.rxue.investment.interfaces.web;

import java.time.LocalDate;

public record ReturnValue(LocalDate time, double value) {
}

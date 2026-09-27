package io.github.rxue.investment.adapter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public enum Account {
    OP( StandardCharsets.ISO_8859_1),NORDNET(StandardCharsets.ISO_8859_1);
    private final Charset csvCharset;

    Account(Charset csvCharset) {
        this.csvCharset = csvCharset;
    }

    public Charset csvCharset() {
        return csvCharset;
    }
}

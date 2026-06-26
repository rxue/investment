package io.github.rxue.investment.application;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class YahooCompanySymbolRepository {
    private static final Map<String, String> opCompanySymbolToYahoo = loadCompanySymbolMap();

    private static Map<String, String> loadCompanySymbolMap() {
        try (InputStream is = YahooCompanySymbolRepository.class.getClassLoader().getResourceAsStream("companies.csv");
             Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .build()
                     .parse(reader)) {
            return parser.getRecords().stream()
                    .collect(Collectors.toMap(r -> r.get("OP Company Symbol"), r -> r.get("Yahoo Company Symbol")));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    Optional<String> findBy(String opSymbol) {
        return Optional.ofNullable(opCompanySymbolToYahoo.get(opSymbol));
    }
}

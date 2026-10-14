package io.github.rxue.investment.adapter.transaction.csv.op;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QualifiedTickerRepository {
    private static final Map<String,String> TICKER_CACHE = loadTickers("/companies.csv", "op_security_id");
    private static final Map<String,String> TICKER_BY_ISIN_CACHE = loadTickers("/companies.csv", "isin");
    private static Map<String,String> loadTickers(String resource, String keyColumn) {
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setDelimiter(',')
                .setHeader()                 // read column names from the first line
                .setSkipHeaderRecord(true)
                .get();
        URL url = QualifiedTickerRepository.class.getResource(resource);
        if (url == null) {
            throw new IllegalStateException("Resource not found on classpath: " + resource);
        }
        try (CSVParser parser = CSVParser.parse(url, StandardCharsets.UTF_8, format)) {
            Map<String,String> tickers = new HashMap<>();
            for (CSVRecord record: parser) {
                tickers.put(record.get(keyColumn), record.get("yahoo_qualified_ticker"));
            }
            return Collections.unmodifiableMap(tickers);
        } catch (IOException ex) {
            throw new UncheckedIOException("Cannot read " + resource, ex);
        }
    }
    String findQualifiedTicker(String opSecurityIdentifier) {
        return TICKER_CACHE.get(opSecurityIdentifier);
    }
    String findQualifiedTickerByIsin(String isin) {
        return TICKER_BY_ISIN_CACHE.get(isin);
    }
}

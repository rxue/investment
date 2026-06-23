package io.github.rxue.investment.portfolio.io;

import io.github.rxue.investment.OPTransaction;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TransactionExtractor {
    private static List<OPTransaction> extract(InputStream inputStream) throws IOException {
        final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        final Charset encoding = Charset.forName("ISO-8859-1");
        final String fieldValueDate = "Arvopäivä";
        final String fieldAmount = "Määrä EUROA";
        final String fieldCategory = "Laji";
        final String fieldExplanation = "Selitys";
        final String fieldMessage = "Viesti";
        try (Reader reader = new InputStreamReader(inputStream, encoding);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setDelimiter(';')
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .build()
                     .parse(reader)) {
            return parser.getRecords().stream()
                    .map(r -> new OPTransaction(
                            LocalDate.parse(r.get(fieldValueDate).trim(), dateFormat),
                            Double.parseDouble(r.get(fieldAmount).trim().replace("+", "").replace(",", ".")),
                            Integer.parseInt(r.get(fieldCategory).trim()),
                            r.get(fieldExplanation).trim(),
                            r.get(fieldMessage).trim()
                    ))
                    .toList();
        }
    }
    public List<OPTransaction> extract(List<Path> csvFilePaths) {
        return csvFilePaths.stream()
                .map(filePath -> {
                    try {
                        return extract(Files.newInputStream(filePath));
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .flatMap(List::stream)
                .toList();
    }
}

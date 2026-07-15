package io.github.rxue.investment.adapter.op;

import io.github.rxue.investment.portfolio.transaction.Trade;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

class OPTransactionExtractor {
    private static List<OPTransaction> extractPaths(InputStream inputStream) throws IOException {
        final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        final Charset encoding = Charset.forName("ISO-8859-1");
        final String fieldValueDate = "Arvopäivä";
        final String fieldAmount = "Määrä EUROA";
        final String fieldCategory = "Laji";
        final String fieldExplanation = "Selitys";
        final String fieldReceiver = "Saaja/Maksaja";
        final String fieldMessage = "Viesti";
        try (Reader reader = new InputStreamReader(inputStream, encoding);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setDelimiter(';')
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .build()
                     .parse(reader)) {
            return parser.getRecords().stream()
                    .map(r -> new OPTransaction.Builder()
                            .effectiveDate(LocalDate.parse(r.get(fieldValueDate).trim(), dateFormat))
                            .amountInEuro(new BigDecimal(r.get(fieldAmount).trim().replace("+", "").replace(",", ".")))
                            .category(Integer.parseInt(r.get(fieldCategory).trim()))
                            .explanation(r.get(fieldExplanation).trim())
                            .counterpart(r.get(fieldReceiver))
                            .message(r.get(fieldMessage).trim())
                            .build())
                    .toList();
        }
    }

    public List<OPTransaction> extract(List<InputStream> csvInputStreams) {
        return csvInputStreams.stream()
                .map(is -> {
                    try(InputStream csvInputStream = is) {
                        return extractPaths(csvInputStream);
                    } catch(IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .flatMap(List::stream)
                .toList();
    }

}

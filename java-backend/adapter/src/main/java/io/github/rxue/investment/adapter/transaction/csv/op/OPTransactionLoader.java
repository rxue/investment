package io.github.rxue.investment.adapter.transaction.csv.op;

import io.github.rxue.investment.adapter.TransactionLoader;
import io.github.rxue.investment.portfolio.transactions.Transaction;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.github.rxue.investment.adapter.Account.OP;

public class OPTransactionLoader implements TransactionLoader {
    private final QualifiedTickerRepository qualifiedTickerRepository;
    private final Path csvDirectoryPath;
    OPTransactionLoader(QualifiedTickerRepository qualifiedTickerRepository, Path csvDirectoryPath) {
        this.qualifiedTickerRepository = qualifiedTickerRepository;
        this.csvDirectoryPath = csvDirectoryPath;
    }
    public OPTransactionLoader(Path csvDirectoryPath) {
        this(new QualifiedTickerRepository(), csvDirectoryPath);
    }

    private static OPTransaction toCustomTransaction(CSVRecord csvRecord) {
        return new OPTransaction(csvRecord.get("Kirjauspäivä"),
                csvRecord.get("Määrä EUROA"),
                csvRecord.get("Laji"),
                csvRecord.get("Selitys"),
                csvRecord.get("Viesti"));
    }
    private static List<OPTransaction> loadOPTransactions(Path csvPath) {
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setDelimiter(';')
                .setHeader()                 // read column names from the first line
                .setSkipHeaderRecord(true)
                .get();
        try (Reader reader = Files.newBufferedReader(csvPath, OP.csvCharset());
             CSVParser parser = format.parse(reader)) {
            return parser.stream()
                    .map(OPTransactionLoader::toCustomTransaction)
                    .toList();
            } catch (IOException ex) {
                throw new UncheckedIOException("", ex);
        }
    }
    @Override
    public List<Transaction> load() {
        if (Files.isDirectory(csvDirectoryPath)) {
            List<Path> csvPaths = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(csvDirectoryPath, "*.{csv,CSV}")) {
                stream.forEach(csvPaths::add);
            } catch (IOException ex) {
                throw new UncheckedIOException("Cannot list " + csvDirectoryPath, ex);
            }
            Collections.sort(csvPaths); // directory order is not guaranteed
            return csvPaths.stream()
                    .map(OPTransactionLoader::loadOPTransactions)
                    .flatMap(List::stream)
                    .map(opTr -> opTr.toTransaction(qualifiedTickerRepository))
                    .toList();
        } else {
            return loadOPTransactions(csvDirectoryPath).stream()
                    .map(opTr -> opTr.toTransaction(qualifiedTickerRepository))
                    .toList();
        }
    }
}

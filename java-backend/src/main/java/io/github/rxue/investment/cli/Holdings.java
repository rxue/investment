package io.github.rxue.investment.cli;

import de.vandermeer.asciitable.AsciiTable;
import io.github.rxue.investment.adapter.op.OPHoldingsGenerator;
import io.github.rxue.investment.portfolio.holdings.Holding;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Command(name = "HOLDINGS")
class Holdings implements Runnable {
    @Option(names = "--fields", split = ",")
    Set<String> fieldNames = Set.of();

    @Parameters(index = "0", description = "Directory containing transaction CSV files")
    Path directory;

    @Override
    public void run() {
        OPHoldingsGenerator holdingsGenerator = new OPHoldingsGenerator();
        List<Holding> holdings = holdingsGenerator.generate(csvInputStreams(directory), fieldNames);

        AsciiTable table = new AsciiTable();
        table.addRule();
        table.addRow("Company", "Shares", "Price (EUR)");
        table.addRule();
        for (Holding h : holdings) {
            table.addRow(
                    h.getCompanyIdentifier(),
                    h.getPosition(),
                    h.getPriceInEuro().value());
        }
        table.addRule();
        System.out.println(table.render());
    }

    static List<InputStream> csvInputStreams(Path directory) {
        try {
            return Files.list(directory)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".csv"))
                    .sorted(Comparator.comparing(p -> p.getFileName().toString()))
                    .map(p -> {
                        try {
                            return Files.newInputStream(p);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

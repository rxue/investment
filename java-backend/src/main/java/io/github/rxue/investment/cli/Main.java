package io.github.rxue.investment.cli;

import de.vandermeer.asciitable.AsciiTable;
import io.github.rxue.investment.application.OPHoldingsGenerator;
import io.github.rxue.investment.portfolio.holdings.Holding;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Command(subcommands = {Main.Holdings.class})
public class Main {
    public static void main(String[] args) {
        System.exit(new CommandLine(new Main()).execute(args));
    }

    @Command(name = "HOLDINGS")
    static class Holdings implements Runnable {
        @Option(names = "--fields", split = ",")
        Set<String> fieldNames = Set.of();

        @Parameters(index = "0", description = "Directory containing transaction CSV files")
        Path directory;

        @Override
        public void run() {
            List<Path> transactionFiles;
            try (var paths = Files.list(directory)) {
                transactionFiles = paths.filter(Files::isRegularFile)
                        .filter(p -> p.getFileName().toString().endsWith(".csv"))
                        .sorted(Comparator.comparing(p -> p.getFileName().toString()))
                        .toList();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }

            OPHoldingsGenerator holdingsGenerator = new OPHoldingsGenerator();
            List<Holding> holdings = holdingsGenerator.generate(transactionFiles, fieldNames);

            AsciiTable table = new AsciiTable();
            table.addRule();
            table.addRow("Company", "Shares", "Price (EUR)");
            table.addRule();
            for (Holding h : holdings) {
                table.addRow(
                        h.getCompanyIdentifier(),
                        h.getPosition(),
                        h.getPriceInEuro().map(p -> p.value().toString()).orElse("-"));
            }
            table.addRule();
            System.out.println(table.render());
        }
    }
}

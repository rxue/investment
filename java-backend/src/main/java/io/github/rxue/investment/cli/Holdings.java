package io.github.rxue.investment.cli;

import de.vandermeer.asciitable.AsciiTable;
import io.github.rxue.investment.application.op.OPHoldingsGenerator;
import io.github.rxue.investment.portfolio.holdings.Holding;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

@Command(name = "HOLDINGS")
class Holdings implements Runnable {
    private final OPTransactionExtractor opTransactionExtractor = new OPTransactionExtractor();
    @Option(names = "--fields", split = ",")
    Set<String> fieldNames = Set.of();

    @Parameters(index = "0", description = "Directory containing transaction CSV files")
    Path directory;

    @Override
    public void run() {
        OPHoldingsGenerator holdingsGenerator = new OPHoldingsGenerator();
        List<Holding> holdings = holdingsGenerator.generate(opTransactionExtractor.extract(directory), fieldNames);

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
}

package io.github.rxue.investment.cli;

import de.vandermeer.asciitable.AsciiTable;
import io.github.rxue.investment.adapter.op.OPXIRRCalculator;
import io.github.rxue.investment.portfolio.xirr.CashFlowInput;
import io.github.rxue.investment.portfolio.xirr.XIRRResult;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;

import static io.github.rxue.investment.cli.Holdings.csvInputStreams;

@Command(name = "XIRR")
class XIRR implements Runnable {
    @Parameters(index = "0", description = "Directory containing transaction CSV files")
    Path directory;

    @Override
    public void run() {
        XIRRResult result = new OPXIRRCalculator().calculate(csvInputStreams(directory));
        AsciiTable table = new AsciiTable();
        table.addRule();
        table.addRow("Date", "Type", "Value (EUR)");
        table.addRule();
        for (CashFlowInput cashFlow : result.cashFlowList()) {
            table.addRow(
                    cashFlow.getDate(),
                    cashFlow.getType(),
                    cashFlow.getValueInCent() / 100.0);
        }
        table.addRule();
        System.out.println(table.render());
        System.out.println("XIRR: " + result.value());
    }

}

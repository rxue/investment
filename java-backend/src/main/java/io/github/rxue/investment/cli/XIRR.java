package io.github.rxue.investment.cli;

import io.github.rxue.investment.application.op.OPTransaction;
import io.github.rxue.investment.application.op.OPXIRRCalculator;
import io.github.rxue.investment.portfolio.xirr.XIRRResult;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.util.List;

@Command(name = "XIRR")
class XIRR implements Runnable {
    private final OPTransactionExtractor opTransactionExtractor = new OPTransactionExtractor();

    @Parameters(index = "0", description = "Directory containing transaction CSV files")
    Path directory;

    @Override
    public void run() {
        List<OPTransaction> opTransactions = opTransactionExtractor.extract(directory);
        XIRRResult result = new OPXIRRCalculator().calculate(opTransactions);
        System.out.println(result);
        System.out.println(result.value());
    }
}

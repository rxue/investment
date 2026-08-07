package io.github.rxue.investment.cli;

import io.github.rxue.investment.adapter.op.OPTWRCalculator;
import io.github.rxue.investment.adapter.op.OPTransactionExtractor;
import io.github.rxue.investment.portfolio.twr.TWRCalculator;
import io.github.rxue.investment.portfolio.twr.output.TWRResult;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;

import static io.github.rxue.investment.cli.Holdings.csvInputStreams;

@Command(name = "TWR")
class TWR implements Runnable {
    @Parameters(index = "0", description = "Directory containing transaction CSV files")
    Path directory;

    @Override
    public void run() {
        TWRResult result = new OPTWRCalculator(new TWRCalculator(), new OPTransactionExtractor())
                .calculate(csvInputStreams(directory));
        System.out.println(result);
        System.out.println("TWR value: " + result.value());
    }

}

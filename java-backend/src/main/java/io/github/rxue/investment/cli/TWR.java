package io.github.rxue.investment.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;

@Command(name = "TWR")
class TWR implements Runnable {
    @Parameters(index = "0", description = "Directory containing transaction CSV files")
    Path directory;

    @Override
    public void run() {
        //TWRResult result = new OPTWRCalculator().calculate(csvInputStreams(directory));
        //AsciiTable table = new AsciiTable();
    }

}

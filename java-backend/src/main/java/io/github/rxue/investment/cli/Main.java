package io.github.rxue.investment.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "investment-cli", subcommands = {YahooFinanceMetricsCommand.class}, mixinStandardHelpOptions = true)
public class Main {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}

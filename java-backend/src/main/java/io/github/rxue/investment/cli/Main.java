package io.github.rxue.investment.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(subcommands = {Holdings.class, XIRR.class})
public class Main {
    public static void main(String[] args) {
        System.exit(new CommandLine(new Main()).execute(args));
    }
}

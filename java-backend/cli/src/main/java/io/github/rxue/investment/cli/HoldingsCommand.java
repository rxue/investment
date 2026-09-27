package io.github.rxue.investment.cli;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.ColumnData;
import io.github.rxue.investment.adapter.Account;
import io.github.rxue.investment.adapter.CSVTransactionLoader;
import io.github.rxue.investment.portfolio.holdings.Holding;
import io.github.rxue.investment.portfolio.holdings.HoldingsBuilder;
import io.github.rxue.investment.portfolio.transactions.Trade;
import io.github.rxue.investment.portfolio.transactions.Transaction;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "holdings", description = "Fetch holdings from a given csv file or directory storing csv files",
        mixinStandardHelpOptions = true)
public class HoldingsCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "The portfolio bank account, possible values include OP, NORDNET atm")
    private String accountName;

    @Parameters(index = "1", description = "The Path of the CSV file or directory")
    private String csvFileOrDirectoryPath;

    @Spec
    private CommandSpec spec;

    @Override
    public Integer call() {
        Account account = Account.valueOf(accountName);
        CSVTransactionLoader transactionLoader = new CSVTransactionLoader(account, Path.of(csvFileOrDirectoryPath));
        List<Transaction> transactions = transactionLoader.load();
        List<Trade> trades = transactions.stream()
                .filter(Trade.class::isInstance)
                .map(Trade.class::cast)
                .toList();
        Collection<Holding> holdings = new HoldingsBuilder()
                .apply(trades)
                .build();
        List<ColumnData<Holding>> columns = List.of(
                new Column().header("Ticker Symbol").with(Holding::securityId),
                new Column().header("Position").with(h -> String.valueOf(h.position())));
        spec.commandLine().getOut().println(AsciiTable.getTable(holdings, columns));
        return 0;
    }

}

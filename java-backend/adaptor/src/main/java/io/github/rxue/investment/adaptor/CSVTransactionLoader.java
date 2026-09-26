package io.github.rxue.investment.adaptor;

import io.github.rxue.investment.adaptor.transaction.csv.op.OPTransactionLoader;
import io.github.rxue.investment.adaptor.transaction.csv.op.QualifiedTickerRepository;
import io.github.rxue.investment.portfolio.transactions.Transaction;

import java.nio.file.Path;
import java.util.List;

public class CSVTransactionLoader implements TransactionLoader {
    private final Account account;
    private final Path csvOrDirectoryPath;
    public CSVTransactionLoader(Account account, Path csvOrDirectoryPath) {
        this.account = account;
        this.csvOrDirectoryPath = csvOrDirectoryPath;
    }

    @Override
    public List<Transaction> load() {
        return switch (account) {
            case OP -> new OPTransactionLoader(csvOrDirectoryPath).load();
            case NORDNET -> throw new UnsupportedOperationException("Loading " + account + " transactions is not supported yet");
        };
    }
}

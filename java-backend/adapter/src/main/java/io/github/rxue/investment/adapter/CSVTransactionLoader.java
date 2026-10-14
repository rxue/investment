package io.github.rxue.investment.adapter;

import io.github.rxue.investment.vo.bankaccount.transaction.Transaction;
import io.github.rxue.investment.adapter.transaction.csv.op.OPTransactionLoader;

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

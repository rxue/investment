package io.github.rxue.investment.cli;

import com.github.freva.asciitable.AsciiTable;
import io.github.rxue.investment.financialstatement.balancesheet.Assets;
import io.github.rxue.investment.financialstatement.balancesheet.BalanceSheet;
import io.github.rxue.investment.financialstatement.balancesheet.Equity;
import io.github.rxue.investment.pdfgeneration.BalanceSheetGenerator;
import io.github.rxue.investment.vo.bankaccount.transaction.Period;
import io.github.rxue.investment.financialstatement.Type;
import io.github.rxue.investment.financialstatement.IncomeStatement;
import io.github.rxue.investment.adapter.Account;
import io.github.rxue.investment.adapter.CSVTransactionLoader;
import io.github.rxue.investment.pdfgeneration.IncomeStatementGenerator;
import io.github.rxue.investment.vo.bankaccount.transaction.TransactionList;
import picocli.CommandLine.Command;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.TypeConversionException;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.concurrent.Callable;

@Command(name = "financial-statements", description = "Generate a financial statement from a directory storing csv files",
        mixinStandardHelpOptions = true)
public class FinancialStatementsCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "The type of the statement, possible values: ${COMPLETION-CANDIDATES}")
    private Type type;

    @Parameters(index = "1", converter = PeriodConverter.class,
            description = "The period in format startDate:endDate, both dates included, e.g. 2025-07-21:2025-12-31")
    private Period period;

    @Parameters(index = "2", description = "The Path of the directory storing csv files")
    private Path csvDirectoryPath;

    @Option(names = "--account", description = "The portfolio bank account, possible values: ${COMPLETION-CANDIDATES}, default: ${DEFAULT-VALUE}")
    private Account account = Account.OP;

    @Option(names = "--pdf", description = "The Path of the pdf file to generate, the statement is printed to the console when omitted")
    private Path pdfPath;

    @Override
    public Integer call() {
        TransactionList transactions = new CSVTransactionLoader(account, csvDirectoryPath).load();
        switch (type) {
            case INCOME_STATEMENT -> {
                IncomeStatement incomeStatement = new IncomeStatement(period, transactions);
                if (pdfPath == null) {
                    printIncomeStatement(incomeStatement);
                } else {
                    new IncomeStatementGenerator(incomeStatement).generate(pdfPath);
                }
            }
            case BALANCE_SHEET -> {
                BalanceSheet balanceSheet = new BalanceSheet(period.endDate(), transactions);
                if (pdfPath == null) {
                    printBalanceSheet(balanceSheet);
                } else {
                    new BalanceSheetGenerator(balanceSheet).generate(pdfPath);
                }
            }
        }
        return 0;
    }

    private static void printIncomeStatement(IncomeStatement incomeStatement) {
        Period period = incomeStatement.period();
        String[] headers = {"Income statement " + period.startDate() + " - " + period.endDate(), "EUR"};
        String[][] rows = {
                {"Dividend income", incomeStatement.financialIncome().dividend().toPlainString()},
                {"Total expenses", incomeStatement.totalExpenses().toPlainString()}
        };
        System.out.println(AsciiTable.getTable(headers, rows));
    }
    private static void printBalanceSheet(BalanceSheet balanceSheet) {
        LocalDate date = balanceSheet.date();
        System.out.println("Balance Sheet on " + date);
        //Asset section
        String[] headers = {"Asset"};
        Assets assets = balanceSheet.assets();
        String[][] rows = {
                {"Shares as cost", assets.listedSharesAtCost().toPlainString()},
                {"Cash", assets.cash().toPlainString()},
                {"Total", assets.total().toPlainString()}
        };
        System.out.println(AsciiTable.getTable(headers, rows));
        //Equity and liabilities section
        String[] EquityHeader = {"Equity"};
        Equity equity = balanceSheet.equity();
        String[][] equityRows = {
                {"Capital Paid by Owner", equity.capitalPaidByOwner().toPlainString()},
                {"Reatained Earnings", equity.retainedEarnings().toPlainString()},
                {"Profit for the year", equity.profitForTheYear().toPlainString()},
                {"Total", equity.total().toPlainString()}
        };
        System.out.println(AsciiTable.getTable(EquityHeader, equityRows));
    }

    static class PeriodConverter implements ITypeConverter<Period> {
        @Override
        public Period convert(String value) {
            String[] dates = value.split(":");
            if (dates.length != 2) {
                throw new TypeConversionException("expected startDate:endDate, e.g. 2025-07-21:2025-12-31, but was '" + value + "'");
            }
            LocalDate startDate;
            LocalDate endDate;
            try {
                startDate = LocalDate.parse(dates[0]);
                endDate = LocalDate.parse(dates[1]);
            } catch (DateTimeParseException e) {
                throw new TypeConversionException("dates must be in format yyyy-MM-dd, but was '" + value + "'");
            }
            if (endDate.isBefore(startDate)) {
                throw new TypeConversionException("end date " + endDate + " is before start date " + startDate);
            }
            return new Period(startDate, endDate);
        }
    }
}

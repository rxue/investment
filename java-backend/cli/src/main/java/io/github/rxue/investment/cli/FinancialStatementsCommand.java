package io.github.rxue.investment.cli;

import com.github.freva.asciitable.AsciiTable;
import io.github.rxue.investment.financialstatement.FinancialStatements;
import io.github.rxue.investment.financialstatement.balancesheet.Assets;
import io.github.rxue.investment.financialstatement.balancesheet.BalanceSheet;
import io.github.rxue.investment.financialstatement.balancesheet.Equity;
import io.github.rxue.investment.financialstatement.notes.Notes;
import io.github.rxue.investment.portfolio.holdings.Holdings;
import io.github.rxue.investmentutility.vo.Metric;
import io.github.rxue.investmentutility.vo.MetricValues;
import io.github.rxue.investmentutility.vo.bankaccount.transaction.Period;
import io.github.rxue.investment.financialstatement.IncomeStatement;
import io.github.rxue.investment.adapter.Account;
import io.github.rxue.investment.adapter.CSVTransactionLoader;
import io.github.rxue.investmentutility.vo.bankaccount.transaction.TransactionList;
import picocli.CommandLine.Command;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.TypeConversionException;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.Callable;

import static io.github.rxue.investment.Util.toBaseUnit;
import static io.github.rxue.investmentutility.marketquote.QuoteMetric.COMPANY_NAME;
import static java.util.stream.Collectors.joining;

@Command(name = "financial-statements", description = "Generate a financial statement from a directory storing csv files",
        mixinStandardHelpOptions = true)
public class FinancialStatementsCommand implements Callable<Integer> {
    // left-aligned, padded to 10 chars
    private static final String CELL_FORMAT = "%-10s";

    @Parameters(index = "0", converter = PeriodConverter.class,
            description = "The period in format startDate:endDate, both dates included, e.g. 2025-07-21:2025-12-31")
    private Period period;

    @Parameters(index = "1", description = "The Path of the directory storing csv files")
    private Path csvDirectoryPath;

    @Option(names = "--account", description = "The portfolio bank account, possible values: ${COMPLETION-CANDIDATES}, default: ${DEFAULT-VALUE}")
    private Account account = Account.OP;

    @Option(names = "--pdf", description = "The Path of the pdf file to generate, the statement is printed to the console when omitted")
    private Path pdfPath;

    @Override
    public Integer call() {
        TransactionList transactions = new CSVTransactionLoader(account, csvDirectoryPath).load();
        FinancialStatements financialStatements = FinancialStatements.generate(period, transactions);
        IncomeStatement incomeStatement = financialStatements.incomeStatement();
        printIncomeStatement(incomeStatement);
        printBalanceSheet(financialStatements.balanceSheet());
        printNotes(financialStatements.notes());
        return 0;
    }

    private static void printIncomeStatement(IncomeStatement incomeStatement) {
        Period period = incomeStatement.period();
        String[] headers = {"Income statement " + period.startDate() + " - " + period.endDate(), "EUR"};
        IncomeStatement.FinancialIncome financialIncome = incomeStatement.financialIncome();
        String[][] rows = {
                {"Dividend income", financialIncome.dividend().toPlainString()},
                {"Capital gains", financialIncome.capitalGains().toPlainString()},
                {"Total expenses", incomeStatement.totalExpenses().toPlainString()},
                {"Profit/loss", incomeStatement.totalProfit().toPlainString()},

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

    private static void printNotes(Notes notes) {
        var out = System.out;
        out.println();
        out.println("Notes");
        out.println("\tInvestment");
        Holdings holdings = notes.investment()
                .holdings();
        List<Metric> metrics = holdings.metrics();
        String header = metrics.stream().map(Metric::label).map(s -> String.format(CELL_FORMAT, s))
                .collect(joining());
        out.println(header);
        List<String> rows = holdings.rows().stream()
                .map(FinancialStatementsCommand::toHoldingRow)
                .toList();
        rows.forEach(out::println);

    }
    private static String toHoldingRow(MetricValues metricValues) {
        return metricValues.metricList().stream()
                .map(metric -> {
                    Comparable<?> value = metricValues.get(metric);
                    if (metric == COMPANY_NAME)
                        return String.format("%-32s", value);
                    else if (value instanceof Long centValue) {
                        return String.format(CELL_FORMAT, toBaseUnit(centValue));
                    }
                    return String.format(CELL_FORMAT, value);
                })
                .collect(joining());
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

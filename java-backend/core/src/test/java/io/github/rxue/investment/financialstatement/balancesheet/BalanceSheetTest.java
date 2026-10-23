package io.github.rxue.investment.financialstatement.balancesheet;

import io.github.rxue.investmentutility.vo.bankaccount.transaction.Deposit;
import io.github.rxue.investmentutility.vo.bankaccount.transaction.ServiceCharge;
import io.github.rxue.investmentutility.vo.bankaccount.transaction.Transaction;
import io.github.rxue.investmentutility.vo.bankaccount.transaction.TransactionList;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BalanceSheetTest {
    @Test
    public void oneDepositAndOneExpense() {
        List<Transaction> transactions = List.of(
                new Deposit(LocalDate.of(2025, 5, 1), 100000),
                new ServiceCharge(LocalDate.of(2025, 5, 15), -500));
        BalanceSheet balanceSheet = new BalanceSheet(LocalDate.of(2025, 12, 31), new TransactionList(transactions));

        Assets assets = balanceSheet.assets();
        assertEquals(new BigDecimal("0.00"), assets.listedSharesAtCost());
        assertEquals(new BigDecimal("995.00"), assets.cash());

        Equity equity = balanceSheet.equity();
        assertEquals(new BigDecimal("1000.00"), equity.capitalPaidByOwner());
        assertEquals(new BigDecimal("-5.00"), equity.profitForTheYear());
        assertEquals(assets.cash().add(assets.listedSharesAtCost()), equity.total());
    }
}

package io.github.rxue.investment.portfolio.xirr;

import io.github.rxue.investment.OPTransaction;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IRRCalculatorTest {

    @Test
    void extractTransactions() throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream("/transactions.csv")) {
            List<OPTransaction> transactions = XIRRCalculator.extractTransactions(inputStream);
            assertEquals(1, transactions.size());
            OPTransaction transaction = transactions.getFirst();
            assertEquals(LocalDate.of(2025, 7, 21), transaction.effectiveDate());
            assertEquals(5000.0, transaction.amountInEuro());
            assertEquals(710, transaction.category());
            assertEquals("TILISIIRTO", transaction.explanation());
            assertEquals("SEPA-MAKSU                         Alunperin oman yrityksen osakkeiden ostaminen                         NDEAFIHH", transaction.message());
        }
    }
}

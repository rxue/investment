package io.github.rxue.investment.portfolio.xirr;

import io.github.rxue.investment.portfolio.io.TransactionExtractor;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IRRCalculatorTest {

    @Test
    void extractTransactions() throws URISyntaxException {
        /*Path path = Path.of(getClass().getResource("/transactions.csv").toURI());
        List<OPTransaction> transactions = new TransactionExtractor().extract(List.of(path));
        assertEquals(1, transactions.size());
        OPTransaction transaction = transactions.getFirst();
        assertEquals(LocalDate.of(2025, 7, 21), transaction.effectiveDate());
        assertEquals(5000.0, transaction.amountInEuro());
        assertEquals(710, transaction.category());
        assertEquals("TILISIIRTO", transaction.explanation());
        assertEquals("SEPA-MAKSU                         Alunperin oman yrityksen osakkeiden ostaminen                         NDEAFIHH", transaction.message());
*/
    }
}

package io.github.rxue.investment.pdfgeneration;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import io.github.rxue.investment.financialstatement.balancesheet.Assets;
import io.github.rxue.investment.financialstatement.balancesheet.BalanceSheet;
import io.github.rxue.investment.financialstatement.balancesheet.Equity;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BalanceSheetGenerator {
    private static final String COMPANY_NAME = "OutlierX";
    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
    private static final Font SECTION_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private static final Font CELL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font TOTAL_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
    private static final Font FOOTER_FONT = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final BalanceSheet balanceSheet;
    public BalanceSheetGenerator(BalanceSheet balanceSheet) {
        this.balanceSheet = balanceSheet;
    }
    public void generate(Path path) {
        try (OutputStream outputStream = Files.newOutputStream(path)) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            document.add(new Paragraph(COMPANY_NAME, TOTAL_FONT));
            document.add(new Paragraph("Balance Sheet", TITLE_FONT));
            document.add(new Paragraph("As of: " + balanceSheet.date(), CELL_FONT));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Assets", SECTION_FONT));
            document.add(assetsTable(balanceSheet.assets()));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Equity and liabilities", SECTION_FONT));
            document.add(equityTable(balanceSheet.equity()));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Generated at: " + LocalDateTime.now().format(TIMESTAMP_FORMAT), FOOTER_FONT));
            document.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static PdfPTable assetsTable(Assets assets) {
        PdfPTable table = headerTable("", "Amount (EUR)");
        addRow(table, "Listed shares at cost", assets.listedSharesAtCost().toPlainString());
        addRow(table, "Cash", assets.cash().toPlainString());
        addTotalRow(table, "Total assets", assets.listedSharesAtCost().add(assets.cash()));
        return table;
    }

    private static PdfPTable equityTable(Equity equity) {
        PdfPTable table = headerTable("", "Amount (EUR)");
        addRow(table, "Capital paid by owner", equity.capitalPaidByOwner().toPlainString());
        BigDecimal profit = equity.profit();
        addRow(table, "profit/loss", profit.toPlainString());
        BigDecimal totalEquity = equity.total();
        addTotalRow(table, "Total equity", totalEquity);
        BigDecimal totalLiabilities = BigDecimal.ZERO.setScale(totalEquity.scale());
        addTotalRow(table, "Total liabilities", totalLiabilities);
        addTotalRow(table, "Total equity and liabilities", totalEquity.add(totalLiabilities));
        return table;
    }

    private static PdfPTable headerTable(String... headers) {
        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);
        table.setSpacingBefore(6);
        table.setSpacingAfter(6);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Paragraph(header, HEADER_FONT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
        return table;
    }

    private static void addRow(PdfPTable table, String... values) {
        for (String value : values) {
            table.addCell(new PdfPCell(new Paragraph(value, CELL_FONT)));
        }
    }

    private static void addTotalRow(PdfPTable table, String label, BigDecimal amount) {
        table.addCell(new PdfPCell(new Paragraph(label, TOTAL_FONT)));
        table.addCell(new PdfPCell(new Paragraph(amount.toPlainString(), TOTAL_FONT)));
    }
}

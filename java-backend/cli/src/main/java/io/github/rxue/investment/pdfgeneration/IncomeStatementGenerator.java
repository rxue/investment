package io.github.rxue.investment.pdfgeneration;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import io.github.rxue.investment.financialstatement.IncomeStatement;
import io.github.rxue.investmentutility.vo.bankaccount.transaction.Period;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IncomeStatementGenerator {
    private static final String COMPANY_NAME = "OutlierX";
    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private static final Font CELL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font TOTAL_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
    private static final Font FOOTER_FONT = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final IncomeStatement incomeStatement;
    public IncomeStatementGenerator(IncomeStatement incomeStatement) {
        this.incomeStatement = incomeStatement;
    }
    public void generate(Path pdfPath) {
        try (OutputStream outputStream = Files.newOutputStream(pdfPath)) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            document.add(new Paragraph(COMPANY_NAME, TOTAL_FONT));
            document.add(new Paragraph("Income Statement", TITLE_FONT));
            document.add(new Paragraph(periodDescription(incomeStatement.period()), CELL_FONT));
            document.add(new Paragraph(" "));

            document.add(summaryTable(incomeStatement));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Generated at: " + LocalDateTime.now().format(TIMESTAMP_FORMAT), FOOTER_FONT));
            document.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String periodDescription(Period period) {
        return "Period: " + period.startDate() + " to " + period.endDate();
    }

    private static PdfPTable summaryTable(IncomeStatement incomeStatement) {
        PdfPTable table = headerTable("", "Amount (EUR)");
        //addRow(table, "Dividend income", incomeStatement..toPlainString());
        addRow(table, "Total expenses", incomeStatement.totalExpenses().toPlainString());
        BigDecimal profit = incomeStatement.totalProfit();
        table.addCell(new PdfPCell(new Paragraph("Loss/Profit", TOTAL_FONT)));
        table.addCell(new PdfPCell(new Paragraph(profit.toPlainString(), TOTAL_FONT)));
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
}

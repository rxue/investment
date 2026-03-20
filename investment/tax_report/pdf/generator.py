from importlib.resources import files
from pathlib import Path

import PyPDF2
import typst

from investment.tax_report.models import TaxPaidAbroadEntryDTO

_SECURITIES_AND_BOOK_ENTRY_SHARES_TEMPLATE = files("investment.tax_report.pdf").joinpath("securities_and_book_entry_shares.typ")
_TAX_PAID_ABROAD_ENTRY_TEMPLATE = files("investment.tax_report.pdf").joinpath("tax_paid_abroad_entry.typ")
_TAX_PAID_ABROAD_TEMPLATE = files("investment.tax_report.pdf").joinpath("tax_paid_abroad.typ")


def fill_form8a_pdf(input:dict[str,str], output_path: str) -> None:
    print(input)
    path = Path(__file__).parent.parent.parent.parent / "data" / "form8a_26.pdf"
    reader = PyPDF2.PdfReader(open(path, "rb"))
    fields = reader.get_fields() or {}
    writer = PyPDF2.PdfWriter()
    writer.append(reader)
    for code, field in fields.items():
        print(f"{code}: {field.get('/V')}")
    writer.update_page_form_field_values(writer.pages[0], input)
    Path(output_path).parent.mkdir(parents=True, exist_ok=True)
    with open(output_path, "wb") as f:
        writer.write(f)

def generate_tax_paid_abroad(entries: list[TaxPaidAbroadEntryDTO], output_path: str) -> None:
    entry_template = _TAX_PAID_ABROAD_ENTRY_TEMPLATE.read_text(encoding="utf-8")

    tables = "\n\n".join(
        entry_template.format(
            company_name=e.company_name,
            country_of_source=e.country_of_source,
            payment_date_of_foreign_tax=e.payment_date_of_foreign_tax,
            name_of_tax=e.name_of_tax,
            type_of_income=e.type_of_income,
            withholding_rate_according_to_tax_treaty=e.withholding_rate_according_to_tax_treaty,
            conversion_rate_for_currency=e.conversion_rate_for_currency,
            gross_income_from_abroad=e.gross_income_from_abroad,
            expenses_relating_to_income=e.expenses_relating_to_income,
            amount_of_tax_paid_abroad=e.amount_of_tax_paid_abroad,
            amount_of_tax_that_credit_is_claimed_for=e.amount_of_tax_that_credit_is_claimed_for,
            tax_that_credit_is_claimed_for=e.tax_that_credit_is_claimed_for,
            does_tax_treaty_require_tax_sparing_credit=e.does_tax_treaty_require_tax_sparing_credit,
        )
        for e in entries
    )

    source = _TAX_PAID_ABROAD_TEMPLATE.read_text(encoding="utf-8").format(
        tax_paid_abroad_tables=tables
    )
    typst.compile(source.encode(), output=output_path)

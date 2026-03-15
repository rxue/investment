from importlib.resources import files

import typst

from investment.tax_report.models import TaxPaidAbroadEntryDTO

_TAX_PAID_ABROAD_ENTRY_TEMPLATE = files("investment.tax_report.pdf_generation").joinpath("tax_paid_abroad_entry.typ")
_TAX_PAID_ABROAD_TEMPLATE = files("investment.tax_report.pdf_generation").joinpath("tax_paid_abroad.typ")


def generate_tax_paid_abroad(entries: list[TaxPaidAbroadEntryDTO], output_path: str) -> None:
    entry_template = _TAX_PAID_ABROAD_ENTRY_TEMPLATE.read_text(encoding="utf-8")

    tables = "\n\n".join(
        entry_template.format(
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

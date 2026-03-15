from typing import NamedTuple

from investment.accounting.financialstatements.incomestatement.models import DividendPayment


class Country(NamedTuple):
    isin_code:str
    name:str
    income_tax_name:str
    tax_withholding_rate: int = 15

class TaxPaidAbroadEntryDTO(NamedTuple):
    country_of_source: str
    payment_date_of_foreign_tax: str
    name_of_tax: str
    type_of_income: str
    withholding_rate_according_to_tax_treaty: str
    conversion_rate_for_currency: str
    gross_income_from_abroad: str
    expenses_relating_to_income: str
    net_income_from_abroad: str
    amount_of_tax_paid_abroad: str
    amount_of_tax_that_credit_is_claimed_for: str
    tax_that_credit_is_claimed_for: str
    does_tax_treaty_require_tax_sparing_credit: str

    @classmethod
    def from_dividend_payment(cls, payment: DividendPayment, country: Country) -> "TaxPaidAbroadEntryDTO":
        def _to_euro_value(val: float) -> str:
            return f"{val:.2f} EUR"
        return cls(
            country_of_source=country.name,
            payment_date_of_foreign_tax=payment.value_date.strftime("%d.%m.%Y"),
            name_of_tax=country.income_tax_name,
            type_of_income="Dividend from listed company",
            withholding_rate_according_to_tax_treaty=str(country.tax_withholding_rate) + "%",
            conversion_rate_for_currency=str(payment.exchange_rate),
            gross_income_from_abroad=_to_euro_value(payment.gross_value_in_eur),
            expenses_relating_to_income=_to_euro_value(0.0),
            net_income_from_abroad=_to_euro_value(payment.net_value_in_eur),
            amount_of_tax_paid_abroad=_to_euro_value(payment.withholding_tax_in_eur),
            amount_of_tax_that_credit_is_claimed_for=_to_euro_value(payment.withholding_tax_in_eur),
            tax_that_credit_is_claimed_for="Final tax",
            does_tax_treaty_require_tax_sparing_credit="No",
        )

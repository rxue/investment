from typing import NamedTuple
from datetime import date, timedelta

from investment.accounting.financialstatements.incomestatement.models import DividendPayment
from investment.accounting.models import Holding
from investment.market_quote.repository import find_closing_price_by_symbol, find_company_by_op_symbol


class Country(NamedTuple):
    isin_code:str
    name:str
    income_tax_name:str
    tax_withholding_rate: int = 15

class SecurityAndBookEntrySharesDTO(NamedTuple):
    name_of_company_or_cooperative:str
    business_id:str
    share_quantity:str
    undepreciated_acquisition_cost:str
    comparison_value_per_unit:str
    comparison_value_in_total:str

    @classmethod
    def to_SecurityAndBookEntrySharesDTO(cls, holding: Holding, d: date) -> "SecurityAndBookEntrySharesDTO":
        def get_work_date(d: date) -> date:
            while d.weekday() > 4:
                d -= timedelta(days=1)
            return d

        def _to_euro_value(val: float) -> str:
            return f"{val:.2f} €"

        company = find_company_by_op_symbol(holding.symbol)
        price = find_closing_price_by_symbol(company, get_work_date(d))
        comparison_value_per_unit = round(price.price_in_eur() * 0.7, 2)
        return cls(
            name_of_company_or_cooperative=company.short_name,
            business_id="",
            share_quantity=str(holding.share_amount),
            undepreciated_acquisition_cost=_to_euro_value(holding.book_value),
            comparison_value_per_unit=_to_euro_value(comparison_value_per_unit),
            comparison_value_in_total=_to_euro_value(comparison_value_per_unit * holding.share_amount),
        )

class TaxPaidAbroadEntryDTO(NamedTuple):
    company_name: str
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
    def to_TaxPaidAbroadEntryDTO(cls, payment: DividendPayment, country: Country) -> "TaxPaidAbroadEntryDTO":
        def _to_euro_value(val: float) -> str:
            return f"{val:.2f} €"
        return cls(
            company_name=payment.company.name,
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

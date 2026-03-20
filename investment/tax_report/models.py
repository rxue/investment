from dataclasses import dataclass, field
from typing import NamedTuple
from datetime import date, datetime
import yaml

from investment.accounting.financialstatements.incomestatement.models import DividendPayment


class Period(NamedTuple):
    start:date
    end:date
    def start_date_string(self):
        return self.start.strftime("%d%m%Y")
    def end_date_string(self):
        return self.end.strftime("%d%m%Y")
    def to_form8a_string(self):
        return f"{self.start_date_string()}-{self.end_date_string()}"

class Country(NamedTuple):
    isin_code:str
    name:str
    income_tax_name:str
    tax_withholding_rate: int = 15

@dataclass(frozen=True)
class Form8ACompulsoryFields:
    service_provider_id: str
    software_name: str
    software_id: str
    business_id: str
    accounting_period: Period
    timestamp: str = field(default_factory=lambda: datetime.now().strftime("%Y%m%d%H%M%S"))


class ConfigData(NamedTuple):
    company_name: str
    company_id: str
    software_name: str
    service_provider_id: str
    accounting_period: Period
    input_dir: str
    output_dir: str

    def form8a_compulsory_fields(self) -> Form8ACompulsoryFields:
        return Form8ACompulsoryFields(
            service_provider_id=self.service_provider_id,
            software_name=self.software_name,
            software_id="",
            business_id=self.company_id,
            accounting_period=self.accounting_period,
        )
    @classmethod
    def get_config(cls, config_yaml_path: str) -> "ConfigData":
        with open(config_yaml_path) as f:
            cfg = yaml.safe_load(f)
        return cls(
            company_name=cfg["company"]["name"],
            company_id=cfg["company"]["business_id"],
            software_name=cfg["software"],
            service_provider_id=cfg["service_provider_id"],
            accounting_period=Period(*[date.fromisoformat(d) for d in cfg["account_period"].split(":")]),
            input_dir=cfg["input_dir"],
            output_dir=cfg["output_dir"],
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

class Money(NamedTuple):
    euros: str
    cents: str

    @classmethod
    def new(cls, value: float) -> "Money":
        total_cents = round(value * 100)
        return cls(euros=str(total_cents // 100), cents=str(total_cents % 100))
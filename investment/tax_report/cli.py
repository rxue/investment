import argparse
import datetime

import pandas as pd

from investment.accounting.composite_generator import generate
from investment.accounting.csv_to_dataframe import read_csvs_to_dataframe
from investment.accounting.financialstatements.incomestatement.models import DividendPayment
from investment.tax_report.models import Country, TaxPaidAbroadEntryDTO
from investment.tax_report.securites_included_in_financial_assets import SecurityHoldingAsAsset, \
    to_SecurityHoldingsAsAsset


def load_countries(path: str) -> list[Country]:
    df = pd.read_csv(path, keep_default_na=False)
    return [Country(isin_code=row["isin_code"], name=row["country_name"], income_tax_name=row["income_tax_name"]) for _, row in df.iterrows()]


def main():
    parser = argparse.ArgumentParser(description="Generate tax report")
    parser.add_argument("--input-dir", required=True, help="Directory containing CSV files")
    parser.add_argument("--end-date", required=True, help="End date (YYYY-MM-DD)")
    args = parser.parse_args()

    df = read_csvs_to_dataframe(args.input_dir)
    end_date = datetime.date.fromisoformat(args.end_date)
    income_statement, balance_sheet, holdings = generate(df, end_date=end_date)
    countries = load_countries("data/country.csv")

    def get_country_by_isin_code(payment: DividendPayment) -> Country:
        return next(c for c in countries if c.isin_code == payment.get_country_code())

    tax_paid_abroad_entry_dtos = [TaxPaidAbroadEntryDTO.from_dividend_payment(d, get_country_by_isin_code(d)) for d in income_statement.dividend_payments()]
    print("Tax Paid Abroad")
    for tax in tax_paid_abroad_entry_dtos:
        print(tax)
        print()
    securities: list[SecurityHoldingAsAsset] = to_SecurityHoldingsAsAsset(holdings, end_date)

    print(income_statement)
    print(balance_sheet)
    print(f"loss for accounting period is {income_statement.loss()}")
    for security in securities:
        print(security)

import argparse
import datetime
from pathlib import Path
from datetime import date

import pandas as pd

from investment import accounting
from investment.accounting.csv_to_dataframe import read_csvs_to_dataframe
from investment.accounting.financialstatements.incomestatement.models import DividendPayment
from investment.accounting.models import Holding
from investment.tax_report.models import Country, TaxPaidAbroadEntryDTO, SecurityAndBookEntrySharesDTO

def generate_ListOfSecuritiesAndBookEntrySharesDTOs(holdings: list[Holding], date: date) -> list[SecurityAndBookEntrySharesDTO]:
    return [SecurityAndBookEntrySharesDTO.to_SecurityAndBookEntrySharesDTO(holding, date) for holding in holdings]

def generate_TaxPaidAbroadEntryDTOs(dividend_payments:list[DividendPayment]) -> list[TaxPaidAbroadEntryDTO]:
    def _load_countries(path: str) -> list[Country]:
        df = pd.read_csv(path, keep_default_na=False)
        return [Country(isin_code=row["isin_code"], name=row["country_name"], income_tax_name=row["income_tax_name"])
                for _, row in df.iterrows()]

    countries = _load_countries("data/country.csv")
    def get_country_by_isin_code(payment: DividendPayment) -> Country:
        return next(c for c in countries if c.isin_code == payment.get_country_code())
    return [TaxPaidAbroadEntryDTO.to_TaxPaidAbroadEntryDTO(d, get_country_by_isin_code(d)) for d in dividend_payments]


def main():
    import sys

    generate_pdf = len(sys.argv) > 1 and sys.argv[1] == "tax-reports-pdf"
    if generate_pdf:
        sys.argv.pop(1)

    parser = argparse.ArgumentParser(description="Generate tax report")
    parser.add_argument("--input-dir", required=True, help="Directory containing CSV files")
    parser.add_argument("--end-date", required=True, help="End date (YYYY-MM-DD)")
    parser.add_argument("--output-dir", required=True, help="Directory for generated PDF files")
    args = parser.parse_args()

    Path(args.output_dir).mkdir(parents=True, exist_ok=True)

    df = read_csvs_to_dataframe(args.input_dir)
    end_date = datetime.date.fromisoformat(args.end_date)
    income_statement, balance_sheet, holdings = accounting.generator.generate(df, end_date=end_date)

    if generate_pdf:
        from investment.tax_report.pdf_generation import pdf_generator

        securities = generate_ListOfSecuritiesAndBookEntrySharesDTOs(holdings, end_date)
        pdf_generator.generate_ListOfSecuritesAndBookEntrySharesTable(securities, f"{args.output_dir}/list_of_securities_and_book_entry_shares.pdf")

        tax_paid_abroad_entry_dtos = generate_TaxPaidAbroadEntryDTOs(income_statement.dividend_payments())
        print("Tax Paid Abroad")
        pdf_generator.generate_tax_paid_abroad(tax_paid_abroad_entry_dtos, f"{args.output_dir}/tax_paid_abroad.pdf")
    print("Required forms in tax report generated")
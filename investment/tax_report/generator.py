import argparse
from pathlib import Path
from datetime import date

import pandas as pd

from investment import accounting
from investment.accounting.csv_to_dataframe import read_csvs_to_dataframe
from investment.accounting.financialstatements.incomestatement.models import DividendPayment
import investment.tax_report.data_file.generator as formgen
from investment.tax_report.models import Country, TaxPaidAbroadEntryDTO, ConfigData
from investment.tax_report.pdf.generator import fill_form8a_pdf

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
    parser = argparse.ArgumentParser(description="Generate tax report")
    parser.add_argument("command", help="Command to run (e.g. pdf)")
    parser.add_argument("config", help="Path to YAML config file")
    args = parser.parse_args()

    config = ConfigData.get_config(args.config)
    generate_pdf = args.command == "pdf"

    output_dir = config.output_dir
    Path(output_dir).mkdir(parents=True, exist_ok=True)

    df = read_csvs_to_dataframe(config.input_dir)
    end_date = config.accounting_period.end
    income_statement, balance_sheet, holdings = accounting.generator.generate(df, end_date=end_date)

    if generate_pdf:
        form8a_pdf_input_list = formgen.to_form8a_pdf_input(holdings, config.form8a_compulsory_fields())
        idx = 0
        for pdf_input in form8a_pdf_input_list:
            fill_form8a_pdf(pdf_input, output_dir + "/filled_form8a_securities_and_book_entry_shares_" + str(idx) + ".pdf")
            idx = idx + 1
        tax_paid_abroad_entry_dtos = generate_TaxPaidAbroadEntryDTOs(income_statement.dividend_payments())
        print("Tax Paid Abroad")
        #pdf_generator.generate_tax_paid_abroad(tax_paid_abroad_entry_dtos, f"{config.output_dir}/tax_paid_abroad.pdf")
    print("Required forms in tax report generated")
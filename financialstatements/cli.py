import pandas as pd

from financialstatements.balance_sheet import BalanceSheetInCent
from financialstatements.incomestatement.income_item import DividendIncomeInCent
from financialstatements.incomestatement.income_statement import IncomeStatementInCent, generate_income_statement
from financialstatements.calc import get_period, profit_and_book_values_by_symbol, reconcile
from financialstatements.transaction_filters import find_all_stock_tradings_by_symbol, find_dividend_payments, find_expenses, find_cash_infusion


def generate(df: pd.DataFrame) -> tuple[IncomeStatementInCent, BalanceSheetInCent]:
    profit_results = profit_and_book_values_by_symbol(find_all_stock_tradings_by_symbol(df))
    gross_trading_income = sum(r.profit_in_cent for r in profit_results)
    income_statement = generate_income_statement(get_period(df),
                                                 gross_trading_income,
                                                 DividendIncomeInCent(transactions=find_dividend_payments(df)),
                                                 find_expenses(df))
    financial_securities = sum(
        lot.money_amount_in_cent for r in profit_results for lot in r.remaining_lots
    )
    cash = round(df["Määrä EUROA"].str.replace(",", ".").astype(float).sum() * 100)
    balance_sheet = BalanceSheetInCent(cash=cash, financial_securities=financial_securities)
    return income_statement, balance_sheet


def main():
    import argparse
    from financialstatements.csv_to_dataframe import read_csvs_to_dataframe

    parser = argparse.ArgumentParser(description="Generate financial statements from CSV files")
    subparsers = parser.add_subparsers(dest="command", required=True)

    dry_run_parser = subparsers.add_parser("dry-run", help="Print financial statements to stdout")
    dry_run_parser.add_argument("--input-dir", required=True, help="Directory containing CSV files")

    pdf_parser = subparsers.add_parser("pdf", help="Generate financial statements as PDF")
    pdf_parser.add_argument("--input-dir", required=True, help="Directory containing CSV files")

    args = parser.parse_args()

    if args.command == "dry-run":
        df = read_csvs_to_dataframe(args.input_dir)
        income_statement, balance_sheet = generate(df)
        print(income_statement)
        print(balance_sheet)
        print("reconciled" if reconcile(find_cash_infusion(df), income_statement, balance_sheet) else "reconciliation failed")
    elif args.command == "pdf":
        from financialstatements.pdfgeneration.pdf_generator import income_statement_pdf
        df = read_csvs_to_dataframe(args.input_dir)
        income_statement, balance_sheet = generate(df)
        income_statement_pdf(income_statement, "income_statement.pdf")

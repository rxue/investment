#!/usr/bin/env python3
"""Generate tax report as PDF using Typst."""

import argparse
from importlib.resources import files

import typst

from financialstatements.csv_to_dataframe import read_csvs_to_dataframe
from financialstatements.cli import generate
from financialstatements.incomestatement.income_statement import IncomeStatementInCent

_INCOME_STATEMENT_TEMPLATE = files("financialstatements.pdfgeneration").joinpath("income_statement.typ")


def income_statement_pdf(income_statement: IncomeStatementInCent, output_path: str) -> None:
    items = [
        ("Gross Dividend Income", income_statement.gross_dividend_income),
        ("Trading Income", income_statement.trading_income),
        ("Foreign Withholding Tax", income_statement.foreign_withholding_tax),
        ("Service Expense", income_statement.service_expense),
        ("Other Expense", income_statement.other_expense),
    ]

    rows = "\n".join(
        f'    [{label}], [{value_in_cents / 100:,.2f} EUR],'
        for label, value_in_cents in items
    )

    source = _INCOME_STATEMENT_TEMPLATE.read_text(encoding="utf-8").format(rows=rows)
    typst.compile(source.encode(), output=output_path)

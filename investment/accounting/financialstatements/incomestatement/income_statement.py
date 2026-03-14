import pandas as pd

from investment.accounting.financialstatements.incomestatement.models import DividendIncome, ExpensesInCent, IncomeStatement
from investment.accounting.transaction_filters import find_service_charges
from investment.accounting.util import Period


def generate_income_statement(period: Period, gross_trading_income: int, dividend_income: DividendIncome, expenses_df: pd.DataFrame) -> IncomeStatement:
    all_expense_in_cents = abs(round(expenses_df["Määrä EUROA"].str.replace(",", ".").astype(float).sum() * 100))
    service_expense_cents = abs(round(find_service_charges(expenses_df)["Määrä EUROA"].str.replace(",", ".").astype(float).sum() * 100))
    return IncomeStatement(
        period=period,
        dividend_income=dividend_income,
        trading_income_in_cent=gross_trading_income,
        expenses=ExpensesInCent(
            service_expense=service_expense_cents,
            other_expense=all_expense_in_cents - service_expense_cents,
            foreign_withholding_tax=dividend_income.withholding_tax_in_cent(),
        ),
    )

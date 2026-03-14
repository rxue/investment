import pandas as pd

from investment.accounting.financialstatements.balance_sheet import BalanceSheetInCent
from investment.accounting.financialstatements.incomestatement.models import IncomeStatement


def reconcile(cash_infusion_df: pd.DataFrame, income_statement: IncomeStatement, balance_sheet: BalanceSheetInCent) -> bool:
    cash_infused = round(cash_infusion_df["Määrä EUROA"].str.replace(",", ".").astype(float).sum() * 100)
    return cash_infused + income_statement.net_income() == balance_sheet.cash + balance_sheet.financial_securities




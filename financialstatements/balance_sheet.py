from dataclasses import dataclass


@dataclass
class BalanceSheetInCent:
    cash: int
    financial_securities: int


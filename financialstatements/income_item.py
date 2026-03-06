import re
from abc import ABC, abstractmethod
from dataclasses import dataclass

import pandas as pd


class IncomeItemInCent(ABC):
    """An income item of the Gross Income in the income statement.

    Currently there are 2 types of income items: dividend income and trading income.
    """
    transactions: pd.DataFrame

    @abstractmethod
    def gross_value(self) -> int: ...


@dataclass
class DividendIncomeItem(IncomeItemInCent):
    transactions: pd.DataFrame

    def gross_value(self) -> int:
        return sum(
            DividendIncomeItem._gross_value_per_transaction(detail)
            for detail in self.transactions["Viesti"]
        )

    @staticmethod
    def _gross_value_per_transaction(transaction_detail: str) -> int:
        amount_match = re.search(r"Tuoton määrä\s+([\d,]+)USD", transaction_detail)
        if not amount_match:
            raise ValueError("Could not parse gross amount from transaction detail")
        amount_usd = float(amount_match.group(1).replace(",", "."))
        exchange_rate = DividendIncomeItem._exchange_rate_per_transaction(transaction_detail)
        return int(amount_usd / exchange_rate * 100)

    def withholding_tax(self) -> int:
        return sum(
            DividendIncomeItem.withholding_tax_per_transaction(detail)
            for detail in self.transactions["Viesti"]
        )

    @staticmethod
    def _exchange_rate_per_transaction(transaction_detail: str) -> float:
        rate_match = re.search(r"Val\.kurssi\s+([\d,]+)", transaction_detail)
        if not rate_match:
            raise ValueError("Could not parse exchange rate from transaction detail")
        return float(rate_match.group(1).replace(",", "."))

    @staticmethod
    def withholding_tax_per_transaction(transaction_detail: str) -> int:
        tax_match = re.search(r"Lähdevero\s+\S+\s+%\s+([\d,]+)USD", transaction_detail)
        if not tax_match:
            raise ValueError("Could not parse tax from transaction detail")
        tax_usd = float(tax_match.group(1).replace(",", "."))
        exchange_rate = DividendIncomeItem._exchange_rate_per_transaction(transaction_detail)
        return int(tax_usd / exchange_rate * 100)

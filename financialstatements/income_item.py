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
        raise NotImplementedError

    def deducted_tax(self) -> int:
        raise NotImplementedError

    @staticmethod
    def deducted_tax_per_transaction(transaction_detail: str) -> int:
        tax_match = re.search(r"Lähdevero\s+\S+\s+%\s+([\d,]+)USD", transaction_detail)
        rate_match = re.search(r"Val\.kurssi\s+([\d,]+)", transaction_detail)
        if not tax_match or not rate_match:
            raise ValueError("Could not parse tax or exchange rate from transaction detail")
        tax_usd = float(tax_match.group(1).replace(",", "."))
        exchange_rate = float(rate_match.group(1).replace(",", "."))
        return int(tax_usd / exchange_rate * 100)

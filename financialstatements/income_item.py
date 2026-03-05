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
        raise NotImplementedError

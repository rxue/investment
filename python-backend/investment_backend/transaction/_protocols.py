import csv
from typing import Protocol

from investment.portfolio.transaction import Transaction, Trade, Deposit


class CustomTransaction(Protocol):
    def _to_trade(self) -> Trade:...
    def _to_deposit(self) -> Deposit:...
    def _to_dividend(self):...
    def to_transaction(self) -> Transaction:...

class CSVLoader(Protocol):
    csv_encoding:str
    delimiter:str=";"
    def to_custom_transaction(self, row:dict):...
    def load_from_single_csv(self, csv_path:str) -> list[CustomTransaction]:
        with open(csv_path, encoding=self.csv_encoding, newline="") as f:
            reader = csv.DictReader(f, delimiter=self.delimiter)
            return [self.to_custom_transaction(row) for row in reader]
    def load(self, csv_path:str) -> list[Transaction]:
        custom_transactions = self.load_from_single_csv(csv_path)
        return [custom_tr.to_transaction() for custom_tr in custom_transactions]

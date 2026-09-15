from datetime import date, datetime
from decimal import Decimal, ROUND_HALF_UP
from pathlib import Path
from typing import NamedTuple

from investment.portfolio.transaction import Deposit, Trade, Action, Dividend, InvestmentExpense

from investment_backend.transaction._security_repository import find_yahoo_finance_ticker_symbol_by_isin
from investment_backend.transaction._protocols import CSVLoader, CustomTransaction

_CSV_PATH = Path(__file__).parents[3] / "data" / "companies.csv"

class WithholdingTax(NamedTuple):
    date:date
    money:Decimal
    def cent_value(self) -> int:
        return int((self.money * 100).to_integral_value(rounding=ROUND_HALF_UP))
    def is_external_cashflow(self) -> bool:
        return False

class NordnetTransaction(NamedTuple):
    book_date_value: str
    payment_date_value:str
    transaction_type:str
    isin:str
    share_amount_value:str
    price_value:str
    total_charge_value:str
    total_money_value:str
    def _payment_date(self) -> date:
        return datetime.strptime(self.payment_date_value, "%Y-%m-%d").date()
    def _total_money(self)->Decimal:
        return Decimal(self.total_money_value.replace(",", "."))
    def _to_deposit(self) -> Deposit:
        return Deposit(self._payment_date(), self._total_money())
    def _to_trade(self) -> Trade:
        return Trade(
            security_id=find_yahoo_finance_ticker_symbol_by_isin(self.isin),
            action=Action.BUY if self.transaction_type == "OSTO" else Action.SELL,
            share_amount=int(self.share_amount_value),
            date=self._payment_date(),
            money=self._total_money()
        )
    def _to_dividend(self) -> Dividend:
        return Dividend(
            security_id=find_yahoo_finance_ticker_symbol_by_isin(self.isin),
            share_amount=int(self.share_amount_value),
            date=self._payment_date(),
            money=self._total_money()
        )
    def to_transaction(self):
        if self.transaction_type == "TALLETUS OST.":
            return self._to_deposit()
        elif self.transaction_type in ["OSTO","MYYNTI"]:
            return self._to_trade()
        elif self.transaction_type == "OSINKO":
            return self._to_dividend()
        elif self.transaction_type == "ENNAKKOPIDÄTYS":
            return WithholdingTax(
                date=self._payment_date(),
                money=self._total_money(),
            )
        else:
            return InvestmentExpense(
                date=self._payment_date(),
                money=self._total_money(),
            )



class NordnetTransactionsLoader(CSVLoader):
    def __init__(self, csv_encoding, delimiter: str = ";"):
        self.csv_encoding = csv_encoding
        self.delimiter = delimiter
    def load_from_single_csv(self, csv_path: str) -> list[CustomTransaction]:
        return super().load_from_single_csv(csv_path)[::-1]
    def to_custom_transaction(self, row: dict):
        return NordnetTransaction(
            book_date_value=row["Kirjauspäivä"].strip(),
            payment_date_value=row["Maksupäivä"].strip(),
            transaction_type=row["Tapahtumatyyppi"].strip(),
            isin=row["ISIN"].strip(),
            share_amount_value=row["Määrä"].strip(),
            price_value=row["Kurssi"].strip(),
            total_charge_value=row["Kokonaiskulut"].strip(),
            total_money_value=row["Summa"].strip(),
        )

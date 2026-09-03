import csv
import re
from datetime import date, datetime
from decimal import Decimal
from itertools import chain
from typing import NamedTuple

from investment.portfolio.transaction import Action, Deposit, Dividend, Expense, Trade, Transaction
from investment_backend.op._ticker_symbol_repository import find_yahoo_ticker_symbol

# Matches the "O:<ticker> /<shares>" (buy) / "M:<ticker> /<shares>" (sell) prefix OP puts
# in a trade's message, e.g. "O:PFE US /30" or "M:MRNA /20 578876374313".
_TRADE_PATTERN = re.compile(r"^\s*([OM]):(.+?)\s*/(\d+)")

# Matches the ISIN and share count OP puts in a dividend's message, e.g.
# "...US8299331004...Omistettu määrä             30Kpl...".
_ISIN_PATTERN = re.compile(r"\b([A-Z]{2}[A-Z0-9]{9}\d)\b")
_SHARE_AMOUNT_PATTERN = re.compile(r"Omistettu määrä\s+(\d+)Kpl")


class _OPTransaction(NamedTuple):
    book_date: date
    value_date:date
    amount_in_euro:float
    category:str
    explanation:str
    message:str
    def to_transaction(self) -> Transaction:
        def get_trade() -> Trade | None:
            trade_match = _TRADE_PATTERN.match(self.message)
            if trade_match:
                action = Action.BUY if trade_match.group(1) == "O" else Action.SELL
                return Trade(
                    security_id=find_yahoo_ticker_symbol(trade_match.group(2).strip()),
                    action=action,
                    share_amount=int(trade_match.group(3)),
                    date=self.value_date,
                    money=Decimal(str(self.amount_in_euro)),
                )
            return None

        def get_dividend() -> Dividend | None:
            isin_match = _ISIN_PATTERN.search(self.message)
            share_amount_match = _SHARE_AMOUNT_PATTERN.search(self.message)
            if isin_match and share_amount_match:
                return Dividend(
                    security_id=isin_match.group(1),
                    share_amount=int(share_amount_match.group(1)),
                    date=self.value_date,
                    money=Decimal(str(self.amount_in_euro)),
                )
            return None

        if self.category == "700" and (self.explanation in ["PANO","NOSTO"]):
            return get_trade()
        elif self.category == "710" and self.explanation == "TILISIIRTO":
            return Deposit(date=self.value_date, money=Decimal(str(self.amount_in_euro)))
        elif self.category == "710" and self.explanation == "ARVOPAPERIT":
            return get_dividend()
        return Expense(date=self.value_date, money=Decimal(str(self.amount_in_euro)))

def load_transactions(csv_paths:list[str]) -> list[_OPTransaction]:
    def load_from_single_csv(csv_path:str) -> list[_OPTransaction]:
        date_format = "%d.%m.%Y"
        with open(csv_path, encoding="iso-8859-1", newline="") as f:
            reader = csv.DictReader(f, delimiter=";")
            return [
                _OPTransaction(
                    book_date=datetime.strptime(row["Kirjauspäivä"].strip(), date_format).date(),
                    value_date=datetime.strptime(row["Arvopäivä"].strip(), date_format).date(),
                    amount_in_euro=float(row["Määrä EUROA"].strip().replace("+", "").replace(",", ".")),
                    category=row["Laji"].strip(),
                    explanation=row["Selitys"].strip(),
                    message=row["Viesti"].strip(),
                )
                for row in reader
            ]
    transactions:list[list[_OPTransaction]] = [load_from_single_csv(csv_path) for csv_path in csv_paths]
    return list(chain.from_iterable(transactions))

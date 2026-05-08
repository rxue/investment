from __future__ import annotations
from typing import Literal

from dataclasses import dataclass
from enum import Enum, auto
from typing import NamedTuple
from datetime import date

import pandas as pd

from investment.company import Company
from investment.market_quote.models import Quote


class Bank(Enum):
    NORDEA = auto()
    NORDNET = auto()
    OP = auto()

@dataclass(frozen=True, slots=True)
class Trading:
    company_identifier:str  # any string identifying the company, e.g. company name in Nordea trading lots, ticker symbol in OP
    action:str
    date:date
    amount:int
    trade_price:float


class Holding(NamedTuple):
    company:Company
    amount:int
    def with_quote(self)-> HoldingWithQuote | None:
        quote = self.company.get_latest_quote()
        if quote is None:
            return None
        return HoldingWithQuote(
            holding=self,
            quote=quote)

class HoldingWithQuote(NamedTuple):
    holding: Holding
    quote: Quote

class HoldingsSnapshot(NamedTuple):
    bank:Bank
    holding_with_quote_list:list[HoldingWithQuote]

    def to_dataframe(self) -> pd.DataFrame:
        self.holding_with_quote_list.sort(key=lambda s: s.quote.daily_change_rate(), reverse=True)
        return pd.DataFrame([
            {
                "company": h.holding.company.name,
                "amount": h.holding.amount,
                "price": h.quote.price_value(),
                "Price in EUR": h.quote.price_value_in_euro(),
                "daily_change": h.quote.daily_change_rate_value(),
                "dividend_yield": h.quote.dividend_yield,
                "pe": h.quote.pe,
                "roe": h.quote.roe_value(),
                "timestamp": h.quote.timestamp_repr(),
            }
            for h in self.holding_with_quote_list
        ])

    @staticmethod
    def generate_snapshot(holdings:list[Holding]) -> tuple[HoldingsSnapshot, list[str]]:
        companies_missing_quote = []
        holdings_with_quote = []
        for h in holdings:
            holding_with_quote = h.with_quote()
            if holding_with_quote is None:
                companies_missing_quote.append(h.company.name)
            else:
                holdings_with_quote.append(holding_with_quote)
        return HoldingsSnapshot(Bank.NORDEA, holdings_with_quote), companies_missing_quote

Type = Literal["GAIN", "LOSS"]

class ValuationResult(NamedTuple):
    type: Type
    value:float

@dataclass(frozen=True, slots=True)
class NordeaTradingLot(Trading):
    charge:float
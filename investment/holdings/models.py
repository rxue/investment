from __future__ import annotations
from typing import Literal, Any

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

class Field(Enum):
    COST = ("cost", float)
    ROE = ("roe", float)
    def __init__(self, label: str, type_: type):
        self.label = label
        self.type_ = type_

class Holding(NamedTuple):
    company:Company
    amount:int
    optional_fields:dict[Field,Any] = {}
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
    def market_value_in_euro_cent(self) -> float:
        return self.quote.price_in_euro_cent()*self.holding.amount
    def market_value_in_euro(self) -> float:
        return self.market_value_in_euro_cent() / 100

class HoldingsSnapshot(NamedTuple):
    bank:Bank
    holding_with_quote_list:list[HoldingWithQuote]
    def total_market_value_in_euro_cent(self) -> int:
        return sum([h.market_value_in_euro_cent() for h in self.holding_with_quote_list])

    def to_dataframe(self) -> pd.DataFrame:
        self.holding_with_quote_list.sort(key=lambda s: s.quote.daily_change_rate(), reverse=True)
        def to_dict(holding_with_quote:HoldingWithQuote) -> dict[str,Any]:
            result = {
                "company": holding_with_quote.holding.company.name,
                "amount": holding_with_quote.holding.amount,
                "price": holding_with_quote.quote.price_value(),
                "Price in EUR": holding_with_quote.quote.price_value_in_euro(),
                "daily_change": holding_with_quote.quote.daily_change_rate_value(),
                "market_value_in_euro": holding_with_quote.market_value_in_euro(),
                "stake_by_market_value": f"{holding_with_quote.market_value_in_euro_cent() / self.total_market_value_in_euro_cent() * 100:.2f}%",
                "dividend_yield": holding_with_quote.quote.dividend_yield,
                "pe": holding_with_quote.quote.pe,
                "roe": holding_with_quote.quote.roe_value(),
                "timestamp": holding_with_quote.quote.timestamp_repr(),
            }
            for f, val in holding_with_quote.holding.optional_fields.items():
                result[f.label] = val
            return result
        return pd.DataFrame([to_dict(h) for h in self.holding_with_quote_list])

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
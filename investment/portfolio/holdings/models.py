from __future__ import annotations
from typing import Literal, Any, Protocol

from dataclasses import dataclass
from enum import Enum, auto
from typing import NamedTuple
from datetime import date

import pandas as pd

from investment.data_fetch.models import Company, QuoteFact


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

class Fact(Protocol):
    display_title:str
    data_type:type

class CalculatedFact(Enum):
    DAILY_CHANGE_PERCENTAGE = ("Daily Change %", str)
    COST = ("Cost", float)
    def __init__(self, display_title: str, data_type: type):
        self.display_title = display_title
        self.data_type = data_type

class Holding(NamedTuple):
    company:Company
    position:int
    facts:dict[Fact,Any] = {}

class HoldingsSnapshot(NamedTuple):
    bank:Bank
    holding_list:list[Holding]

    def to_dataframe(self) -> pd.DataFrame:
        #self.holding_list.sort(key=lambda s: s.quote.daily_change, reverse=True)
        def to_dict(holding:Holding) -> dict[str,Any]:
            result = {
                "Company Name": holding.company.name,
                "amount": holding.position,
            }
            for fact, value in holding.facts.items():
                result[fact.display_title] = value
            return result
        return pd.DataFrame([to_dict(h) for h in self.holding_list])

    @staticmethod
    def generate_snapshot(holdings:list[Holding]) -> tuple[HoldingsSnapshot, list[str]]:
        companies_missing_quote = []
        return HoldingsSnapshot(Bank.NORDEA, holdings), companies_missing_quote

Type = Literal["GAIN", "LOSS"]

class ValuationResult(NamedTuple):
    type: Type
    value:float

@dataclass(frozen=True, slots=True)
class NordeaTradingLot(Trading):
    charge:float
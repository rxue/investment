from datetime import date
from enum import Enum
from typing import NamedTuple, Any

from investment.data_fetch.ecb_fetcher import fetch_fx_rate_to_euro


class QuoteFact(Enum):
    PRICE = ("Price", float)
    DAILY_CHANGE = ("Daily Change", float)
    PRICE_CURRENCY = ("Currency", str)
    DIVIDEND_YIELD = ("Dividend Yield", float)
    TRAILING_PE = ("Trailing P/E", float)
    TRAILING_EPS = ("EPS", float)
    ROE = ("ROE", float)

    def __init__(self, display_title: str, data_type: type):
        self.display_title = display_title
        self.data_type = data_type

    def yfinance_key(self) -> str:
        match self:
            case QuoteFact.PRICE:           return "currentPrice"
            case QuoteFact.DAILY_CHANGE:    return "regularMarketChange"
            case QuoteFact.PRICE_CURRENCY:  return "currency"
            case QuoteFact.DIVIDEND_YIELD:  return "dividendYield"
            case QuoteFact.TRAILING_PE:     return "trailingPE"
            case QuoteFact.TRAILING_EPS:    return "trailingEps"
            case QuoteFact.ROE:             return "returnOnEquity"

class Company(NamedTuple):
    yahoo_symbol: str
    name: str

def _decimal_to_percentage(decimal_val: float) -> str:
    return f"{decimal_val * 100:.2f}%"

class ClosePrice(NamedTuple):
    value: float
    currency: str
    date:date
    def in_euro(self)->float:
        _, fx_rate = fetch_fx_rate_to_euro(self.currency, self.date)
        result = self.value/fx_rate
        if self.currency == "GBP":
            return result/100
        return result
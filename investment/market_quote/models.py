import math
from datetime import datetime,date
from typing import NamedTuple

from investment.market_quote.ecb_fetcher import fetch_fx_rate_to_euro


def _decimal_to_percentage(decimal_val: float) -> str:
    return f"{decimal_val * 100:.2f}%"

class ClosePrice(NamedTuple):
    date:date
    currency: str
    value: float
    def in_euro(self)->float:
        _, fx_rate = fetch_fx_rate_to_euro(self.currency, self.date)
        result = self.value/fx_rate
        if self.currency == "GBP":
            return result/100
        return result

class Price(NamedTuple):
    currency: str
    value: float
    fx_rate: float | None = None
    def price_value(self) -> str:
        return f"{self.value:<10}{self.currency}"
    def price_in_eur(self)->float:
        result = self.value/self.fx_rate
        if self.currency == "GBP":
            return result/100
        return result


class Quote(NamedTuple):
    price:Price
    dividend_yield:float
    daily_change:float
    timestamp:datetime
    pe:int
    roe:float

    def price_in_euro(self) -> float:
        return self.price.price_in_eur()
    def price_in_euro_cent(self) -> int:
        return int(self.price.price_in_eur()*100)
    def price_value(self) -> str:
        return self.price.price_value()
    def price_value_in_euro(self) -> str:
        return f"{self.price.price_in_eur():.2f}"
    def daily_change_rate(self)->float:
        return self.daily_change / (self.price.value-self.daily_change)
    def daily_change_rate_value(self)->str:
        return _decimal_to_percentage(self.daily_change_rate())
    def roe_value(self) -> str:
        if self.roe is None or math.isnan(self.roe):
            return "-"
        return _decimal_to_percentage(self.roe)
    def timestamp_repr(self) -> str:
        return self.timestamp.strftime("%Y-%m-%d %H:%M %Z")
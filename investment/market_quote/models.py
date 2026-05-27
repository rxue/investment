from datetime import date
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
from pathlib import Path
from typing import NamedTuple

import pandas as pd

from investment.portfolio.holdings.models.holdings import HoldingWithQuote
from investment.portfolio.holdings.holdings_extractor import extract_from


class HoldingsSnapshot(NamedTuple):
    bank: str
    holdings: list[HoldingWithQuote]


    def to_dataframe(self) -> pd.DataFrame:
        return pd.DataFrame([
            {
                "company": s.to_position.company_name,
                "amount": s.to_position.position,
                "price": s.quote.price,
                "currency": s.quote.currency,
                "daily_change": s.quote.daily_change_rate(),
                "dividend_yield": s.quote.dividend_yield,
                "pe": s.quote.pe,
                "roe": s.quote.roe,
                "timestamp": s.quote.timestamp_repr(),
            }
            for s in self.holdings
        ])

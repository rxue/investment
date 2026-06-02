from datetime import date, timedelta

import pandas as pd
import yfinance as yf

from investment.data_fetch.models import ClosePrice, QuoteFact



def get_close_price(symbol: str, date: date) -> ClosePrice | None:
    try:
        ticker = yf.Ticker(symbol)
        hist = ticker.history(
            start=(date - timedelta(days=5)).isoformat(),
            end=(date + timedelta(days=1)).isoformat(),
        )
        if hist.empty:
            return None
        row = hist.iloc[-1]
        currency = ticker.info.get("currency")
        return ClosePrice(date=row.name.date(), currency=currency.upper(), value=float(row["Close"]))
    except Exception:
        return None

def get_index_quote(symbol: str) -> pd.DataFrame | None:
    try:
        return yf.Ticker(symbol).history(period="10y")
    except Exception:
        return None

def find_latest_quote_facts(symbol:str, *facts:QuoteFact) -> dict[QuoteFact,float]:
    def get_value(info:dict, fact:QuoteFact):
        return info.get(fact.yfinance_key())
    try:
        ticker = yf.Ticker(symbol)
        info = ticker.info
        return {fact: get_value(info, fact) for fact in facts}
    except Exception:
        return None
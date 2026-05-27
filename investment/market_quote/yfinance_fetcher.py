from datetime import date, datetime, timedelta
from typing import NamedTuple
from zoneinfo import ZoneInfo

import pandas as pd
import yfinance as yf

from investment.market_quote.ecb_fetcher import fetch_fx_rate_to_euro
from investment.market_quote.models import ClosePrice


class YFinanceQuote(NamedTuple):
    company_symbol: str
    price:float
    currency:str
    dividend_yield:float
    daily_change:float
    timestamp:datetime
    pe:int
    roe:float
    def price_in_euro_cent(self) -> int:
        def fx_rate_date():
            today = date.today()
            quote_date = self.timestamp.date()
            if quote_date == today:
                return today - timedelta(days=1)
            elif quote_date < today:
                return quote_date
        def to_cent(price:float):
            return int(round(price * 100))
        if self.currency != "EURO":
            _, fx_rate = fetch_fx_rate_to_euro(self.currency, fx_rate_date())
            return to_cent(self.price / fx_rate)
        else:
            return to_cent(self.price)
    def roe_value(self) -> str:
        return f"{self.roe}"
    def timestamp_repr(self) -> str:
        return f"{self.timestamp}"

    def price_value_in_euro(self):
        return f"{self.price_in_euro_cent()/100:.2f}"

    def daily_change_rate_value(self):
        return f"{self.daily_change:.2f}%"

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

def get_latest_quote(symbol: str) -> YFinanceQuote | None:
    try:
        ticker = yf.Ticker(symbol)
        info = ticker.info
        if len(info) <= 1:
            return None
        price = info.get("currentPrice")
        currency = info.get("currency")
        dividend_yield = info.get("dividendYield")
        daily_change = info.get("regularMarketChange")
        market_time = info.get("regularMarketTime")
        time_zone = info.get("exchangeTimezoneName")
        pe = info.get("trailingPE")
        roe = info.get("returnOnEquity")
        if price is None or currency is None or daily_change is None or market_time is None or time_zone is None:
            return None
        return YFinanceQuote(company_symbol=symbol,
                             price=price,
                             currency=currency.upper(),
                             dividend_yield=dividend_yield,
                             daily_change=daily_change,
                             timestamp=datetime.fromtimestamp(market_time, tz=ZoneInfo(time_zone)),
                             pe=int(round(pe)) if pe is not None else None,
                             roe=roe)
    except Exception:
        return None

def get_quote(symbol: str, date: date | None = None) -> YFinanceQuote | None:
    if date is None:
        return get_latest_quote(symbol)
    else:
        try:
            ticker = yf.Ticker(symbol)
            hist = ticker.history(start=date.isoformat(), end=(date + timedelta(days=1)).isoformat())
            if hist.empty:
                return None
            info = ticker.info
            row = hist.iloc[0]
            currency = info.get("currency")
            if currency is None:
                return None
            return YFinanceQuote(company_symbol=symbol,
                                 price=float(row["Close"]),
                                 currency=currency.upper(),
                                 dividend_yield=info.get("dividendYield"),
                                 daily_change=float(row["Close"] - row["Open"]),
                                 timestamp=row.name.to_pydatetime(),
                                 pe=int(round(pe)) if (pe := info.get("trailingPE")) is not None else None,
                                 roe=info.get("returnOnEquity"))
        except Exception:
            return None

def get_index_quote(symbol: str) -> pd.DataFrame | None:
    try:
        return yf.Ticker(symbol).history(period="10y")
    except Exception:
        return None
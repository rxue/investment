from datetime import datetime
from typing import NamedTuple
from zoneinfo import ZoneInfo

import yfinance as yf

from investment.market_quote.models import Quote


class YFinanceQuote(NamedTuple):
    price:float
    currency:str
    dividend_yield:float
    daily_change:float
    timestamp:datetime
    pe:int
    roe:float

def get_latest_quote(symbol: str) -> YFinanceQuote | None:
    try:
        info = yf.Ticker(symbol).info
    except Exception as e:
        return None
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
    return YFinanceQuote(price=price,
                 currency=currency.upper(),
                 dividend_yield=dividend_yield,
                 daily_change=daily_change,
                 timestamp=datetime.fromtimestamp(market_time, tz=ZoneInfo(time_zone)),
                 pe=int(round(pe)) if pe is not None else None,
                 roe=roe)

def get_latest_quotes(*symbols: str) -> list[Quote]:
    return [quote for symbol in symbols if (quote := get_latest_quote(symbol)) is not None]

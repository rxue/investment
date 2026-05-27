from datetime import date
from investment.market_quote import yfinance_fetcher

def get_price_in_euro(symbol:str, date:date) -> float:
    yfinance_quote = yfinance_fetcher.get_quote(symbol)

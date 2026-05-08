from dateutil.utils import today

from investment.market_quote import yfinance_fetcher
from investment.market_quote.ecb_fetcher import fetch_fx_rate_to_euro
from investment.market_quote.models import Quote, Price


def get_latest_quote(symbol: str) -> Quote | None:
    yfinance_quote = yfinance_fetcher.get_latest_quote(symbol)
    if yfinance_quote is None:
        return None
    currency = yfinance_quote.currency
    date, fx_rate = fetch_fx_rate_to_euro(base_currency=currency, date=today().date())
    return Quote(
        price=Price(currency=currency, value=yfinance_quote.price, fx_rate=fx_rate),
        dividend_yield=yfinance_quote.dividend_yield,
        daily_change=yfinance_quote.daily_change,
        timestamp=yfinance_quote.timestamp,
        pe=yfinance_quote.pe,
        roe=yfinance_quote.roe,
    )
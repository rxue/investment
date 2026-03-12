from datetime import datetime

from investment.market_quote.ecb_fetcher import fetch_fx_rate_to_euro


def test__fetch_fx_rate_to_euro__eur_is_1():
    rate = fetch_fx_rate_to_euro("EUR", datetime(2026, 2, 27))
    assert rate == 1

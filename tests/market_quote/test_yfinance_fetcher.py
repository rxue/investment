from investment.market_quote.yfinance_fetcher import get_quote

def test_get_latest_quote_for_base_case():
    result = get_quote("FORTUM.HE")
    assert result is not None

def test_get_latest_quote_returns_none_for_nonexistent_symbol():
    result = get_quote("FORTUM")
    assert result is None

def test_get_latest_quote_returns_none_for_malformed_symbol():
    result = get_quote("https://ir.brightstarlottery.com/stock-info/dividend-history/default.aspx")
    assert result is None

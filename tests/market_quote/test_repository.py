

def test__get_symbol():
    from investment.market_quote.repository import _get_symbol
    symbol = _get_symbol("PFE")
    assert symbol == "PFE"

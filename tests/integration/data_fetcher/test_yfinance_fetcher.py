import pytest

from investment.data_fetch.models import QuoteFact
from investment.data_fetch.yfinance_fetcher import find_latest_quote_facts

@pytest.mark.integration
def test_find_company_facts():
    facts_map: dict[QuoteFact, any] = find_latest_quote_facts("PFE", *QuoteFact)
    assert facts_map[QuoteFact.PRICE] > 0
    assert isinstance(facts_map[QuoteFact.DAILY_CHANGE], float)
    assert facts_map[QuoteFact.PRICE_CURRENCY] == "USD"
    assert facts_map[QuoteFact.DIVIDEND_YIELD] > 0
    assert facts_map[QuoteFact.TRAILING_PE] > 0
    assert facts_map[QuoteFact.TRAILING_EPS] > 0
    assert isinstance(facts_map[QuoteFact.ROE], float)


import sys
from investment.market_quote import main
from investment.market_quote.yfinance_fetcher import get_latest_quote

print(get_latest_quote(sys.argv[1]))

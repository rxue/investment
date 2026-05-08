import sys
from datetime import date, datetime

from dateutil.utils import today

from investment.market_quote.yfinance_fetcher import get_latest_quote
from investment.market_quote.ecb_fetcher import fetch_fx_rate_to_euro

if len(sys.argv) < 2:
    print("Usage: python -m investment.market_quote <command> [args]")
    print("Commands: fx_rate <currency>, <symbol>")
    sys.exit(1)

command = sys.argv[1]

if command == "fx_rate":
    if len(sys.argv) > 4:
        print("Usage: python -m investment.market_quote fx_rate <target_currency>")
        sys.exit(1)
    if len(sys.argv) > 3:
        date = datetime.strptime(sys.argv[3], "%Y-%m-%d").date()
        print(fetch_fx_rate_to_euro(base_currency=sys.argv[2], date=date))
    else:
        print(fetch_fx_rate_to_euro(base_currency=sys.argv[2]), date=today())
else:
    print(get_latest_quote(command))

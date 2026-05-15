import sys
from datetime import date, datetime
from os import close

from dateutil.utils import today

from investment.market_quote.yfinance_fetcher import get_quote, get_quotes, get_index_quote, get_close_price
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
        print(fetch_fx_rate_to_euro(base_currency=sys.argv[2], date=today()))
elif command == "equity":
    next_argument = sys.argv[2]
    if next_argument == "close_price":
        symbols = sys.argv[3]
        close_date = datetime.strptime(sys.argv[4], "%Y-%m-%d").date() if len(sys.argv) > 3 else None
        start = datetime.now()
        result = get_close_price(*symbols.split(","), date=close_date)
        print(result)
    else:
        symbols = sys.argv[2]
        close_date = datetime.strptime(sys.argv[3], "%Y-%m-%d").date() if len(sys.argv) > 3 else None
        start = datetime.now()
        result = get_quotes(*symbols.split(","), multithreads=True, date=close_date)
        elapsed = datetime.now() - start
        for r in result:
            print(r)
        print(f"Execution time: {elapsed}")

elif command == "index":
    symbol = sys.argv[2]
    result = get_index_quote(symbol)
    print(result)
    if result is not None:
        import matplotlib
        matplotlib.use("WebAgg")
        import matplotlib.pyplot as plt
        result["Close"].plot(title=symbol)
        plt.show()

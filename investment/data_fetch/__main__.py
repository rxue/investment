import sys
from datetime import date, datetime
from dateutil.utils import today

from investment.data_fetch.models import CompanyFact
from investment.data_fetch.yfinance_fetcher import get_index_quote, get_close_price, find_latest_quote_facts
from investment.data_fetch.ecb_fetcher import fetch_fx_rate_to_euro

if len(sys.argv) < 2:
    print("Usage: python -m investment.data_fetch <command> [args]")
    print("Commands: fx_rate <currency>, <symbol>")
    sys.exit(1)

command = sys.argv[1]

if command == "fx_rate":
    if len(sys.argv) > 4:
        print("Usage: python -m investment.data_fetch fx_rate <target_currency>")
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
        close_date = datetime.strptime(sys.argv[4], "%Y-%m-%d").date() if len(sys.argv) > 4 else today().date()
        for symbol in symbols.split(","):
            result = get_close_price(symbol, close_date)
            print(result)
elif command == "company":
    yf_company_symbol = sys.argv[2]
    facts = sys.argv[3].split(",")
    company_facts = find_latest_quote_facts(yf_company_symbol, *[CompanyFact[fact] for fact in facts])
    print(f"{company_facts}")

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

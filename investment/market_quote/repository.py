import csv
import sys
from datetime import datetime
from pathlib import Path

from investment.market_quote.ecb_fetcher import fetch_fx_rate_to_euro
from investment.market_quote.yahoo_finance_fetcher import fetch_closing_price, fetch_company
from investment.market_quote.models import Company, Price

_SYMBOL_MAP_PATH = Path(__file__).parent / "data" / "symbol_map.csv"


def _get_symbol(op_symbol:str) -> str:
    symbol_map = {}
    with _SYMBOL_MAP_PATH.open() as f:
        symbol_map = {row["OP Bank"]: row["Yahoo Finance"] for row in csv.DictReader(f)}
    return symbol_map.get(op_symbol, op_symbol)


def _find_company_by_op_symbol(op_symbol: str) -> Company:
    yahoo_symbol = _get_symbol(op_symbol)
    return fetch_company(yahoo_symbol)

def find_closing_prices_by_symbols(op_symbols: list[str], date: datetime) -> list[Price]:
    result = []
    for op_symbol in op_symbols:
        company = _find_company_by_op_symbol(op_symbol)
        result.append(Price(company=company,
                            price=fetch_closing_price(company, date),
                            fx_rate=fetch_fx_rate_to_euro(company.currency, date)))
    return result


def main():
    match sys.argv[1]:
        case "closing_price":
            symbols = sys.argv[2].split(",")
            date = datetime.strptime(sys.argv[3], "%Y-%m-%d")
            print(find_closing_prices_by_symbols(symbols, date))
        case "company":
            print(_find_company_by_op_symbol(sys.argv[2]))
        case "fx_rate":
            base_currency = sys.argv[2]
            date = datetime.strptime(sys.argv[3], "%Y-%m-%d")
            print(fetch_fx_rate_to_euro(base_currency, date))
        case _:
            print(f"Unknown method: {sys.argv[1]}", file=sys.stderr)

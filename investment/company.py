import csv
from pathlib import Path
from typing import NamedTuple

from investment.market_quote.fetcher import Quote, get_latest_quote


class Company(NamedTuple):
    quote_fetch_code:str
    name:str
    def get_latest_quote(self) -> Quote | None:
        return get_latest_quote(self.quote_fetch_code)

_CSV_PATH = Path(__file__).parents[1] / "data" / "companies.csv"

def _fill_companies_cache() -> dict[str, Company]:
    result = {}
    with (open(_CSV_PATH, newline="") as f):
        for row in csv.DictReader(f):
            current_company_symbol = row["op_symbol"]
            quote_fetch_code = row["quote_fetch_code_yahoo"]
            name = row["name"]
            company = Company(quote_fetch_code=quote_fetch_code, name=name)
            result[current_company_symbol] = company
            result[name] = company
    return result

companies_cache:Company = _fill_companies_cache()

def find_company_by(symbol:str) -> Company | None:
    return companies_cache.get(symbol)
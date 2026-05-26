import csv
from pathlib import Path
from typing import NamedTuple

class Company(NamedTuple):
    yahoo_symbol:str
    name:str

_CSV_PATH = Path(__file__).parents[1] / "data" / "companies.csv"

def _fill_companies_cache() -> dict[str, Company]:
    result = {}
    with (open(_CSV_PATH, newline="") as f):
        for row in csv.DictReader(f):
            current_company_symbol = row["op_symbol"]
            name = row["name"]
            company = Company(name=name, yahoo_symbol=row["quote_fetch_code_yahoo"])
            result[current_company_symbol] = company
            result[name] = company
    return result

companies_cache:Company = _fill_companies_cache()

def find_company_by(symbol:str) -> Company | None:
    return companies_cache.get(symbol)
import csv
from pathlib import Path
from typing import NamedTuple

_CSV_PATH = Path(__file__).parents[2] / "data" / "companies.csv"

class Security(NamedTuple):
    isin:str
    op_symbol:str
    yahoo_ticker_symbol:str

_security_cache = None

class SecurityCache:
    def __init__(self):
        self.securities = self._load_securities()
        self.isin_to_securities = None
    def build_isin_index(self):
        self.isin_to_securities = {sec.isin:sec for sec in self.securities}

    def _load_securities(self) -> list[Security]:
        with open(_CSV_PATH, newline="") as f:
            return [Security(isin=row["isin"], op_symbol="", yahoo_ticker_symbol=row["yahoo_ticker_symbol"]) for row in csv.DictReader(f)]

def find_yahoo_finance_ticker_symbol_by_isin(isin:str) -> Security:
    global _security_cache
    if _security_cache is None:
        _security_cache = SecurityCache()
    if _security_cache.isin_to_securities is None:
        _security_cache.build_isin_index()
    return _security_cache.isin_to_securities[isin].yahoo_ticker_symbol
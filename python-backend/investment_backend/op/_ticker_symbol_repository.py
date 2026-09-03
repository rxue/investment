import csv
from pathlib import Path

_CSV_PATH = Path(__file__).parents[2] / "data" / "companies.csv"


def _load_op_to_yahoo_ticker_symbols() -> dict[str, str]:
    with open(_CSV_PATH, newline="") as f:
        return {row["op_symbol"]: row["yahoo_ticker_symbol"] for row in csv.DictReader(f)}


_op_to_yahoo_ticker_symbols = _load_op_to_yahoo_ticker_symbols()


def find_yahoo_ticker_symbol(op_ticker_symbol: str) -> str:
    try:
        return _op_to_yahoo_ticker_symbols[op_ticker_symbol]
    except KeyError:
        raise KeyError(f"No yahoo ticker symbol found for OP ticker symbol: {op_ticker_symbol!r}") from None

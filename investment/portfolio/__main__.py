import sys

from investment.portfolio.twr import twr
from investment.text_io import extract_csv

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python -m investment.portfolio <csv_dir>")
        sys.exit(1)
    df = extract_csv(path=sys.argv[1], sep=";", encoding="latin-1")
    print(f"time-weighted return: {twr(df)}")

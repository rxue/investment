import csv
from datetime import date, datetime
from itertools import chain
from typing import NamedTuple


class _OPTransaction(NamedTuple):
    book_date: date
    value_date:date
    amount_in_euro:float
    category:str
    message:str

def load_transactions(csv_paths:list[str]) -> list[_OPTransaction]:
    def load_from_single_csv(csv_path:str) -> list[_OPTransaction]:
        date_format = "%d.%m.%Y"
        with open(csv_path, encoding="iso-8859-1", newline="") as f:
            reader = csv.DictReader(f, delimiter=";")
            return [
                _OPTransaction(
                    book_date=datetime.strptime(row["Kirjauspäivä"].strip(), date_format).date(),
                    value_date=datetime.strptime(row["Arvopäivä"].strip(), date_format).date(),
                    amount_in_euro=float(row["Määrä EUROA"].strip().replace("+", "").replace(",", ".")),
                    category=row["Laji"].strip(),
                    message=row["Viesti"].strip(),
                )
                for row in reader
            ]
    transactions:list[list[_OPTransaction]] = [load_from_single_csv(csv_path) for csv_path in csv_paths]
    return list(chain.from_iterable(transactions))

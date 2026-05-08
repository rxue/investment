import csv
import io
from datetime import date

import requests

def fetch_fx_rate_to_euro(base_currency: str, date: date) -> tuple[date, float]:
    if base_currency == 'EUR':
        return date, 1
    url = f"https://data-api.ecb.europa.eu/service/data/EXR/D.{base_currency}.EUR.SP00.A"
    today = date.today()
    if date < today:
        print(f"date: {date}, today: {today}")
        date_str = date.strftime("%Y-%m-%d")
        response = requests.get(url, params={
            "startPeriod": date_str,
            "endPeriod": date_str,
            "format": "csvdata",
        })
    else:
        response = requests.get(url, params={
            "lastNObservations": 1,
            "format": "csvdata",
        })
    response.raise_for_status()
    reader = csv.DictReader(io.StringIO(response.text))
    row = next(reader)
    return date.fromisoformat(row["TIME_PERIOD"]), float(row["OBS_VALUE"])

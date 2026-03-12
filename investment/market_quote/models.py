from typing import NamedTuple
from zoneinfo import ZoneInfo


class Company(NamedTuple):
    short_name: str
    yahoo_symbol: str
    time_zone: ZoneInfo
    currency: str

class Price(NamedTuple):
    company: Company
    price: float
    fx_rate: float | None = None
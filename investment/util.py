from datetime import date, datetime


def to_date(value: str) -> date:
    return datetime.strptime(value, "%d.%m.%Y").date()

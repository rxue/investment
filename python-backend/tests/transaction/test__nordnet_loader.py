from decimal import Decimal

from investment_backend.transaction._nordnet_loader import NordnetTransaction


def _transaction(total_money_value: str) -> NordnetTransaction:
    return NordnetTransaction(
        book_date="2025-08-07",
        payment_date_value="2025-08-07",
        transaction_type="OSTO",
        isin="US7170811035",
        share_amount=30,
        price=Decimal("20.86"),
        total_charge=Decimal("0"),
        total_money_value=total_money_value,
    )


def test_total_money_positive():
    assert _transaction("2413,32")._total_money() == Decimal("2413.32")


def test_total_money_negative():
    assert _transaction("-1457,99")._total_money() == Decimal("-1457.99")


def test_total_money_whole_number():
    assert _transaction("2000")._total_money() == Decimal("2000")

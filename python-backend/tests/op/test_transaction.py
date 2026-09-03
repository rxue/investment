from datetime import date
from decimal import Decimal

from investment.portfolio.transaction import Action, Deposit, Dividend, Trade

from investment_backend.op.transaction import _OPTransaction


def test_to_transaction_deposit():
    transaction = _OPTransaction(
        book_date=date(2025, 7, 21),
        value_date=date(2025, 7, 21),
        amount_in_euro=7000.0,
        category="710",
        explanation="TILISIIRTO",
        message="SEPA-MAKSU Alunperin oman yrityksen osakkeiden ostaminen",
    )

    result = transaction.to_transaction()

    assert result == Deposit(date=date(2025, 7, 21), money=Decimal("7000"))


def test_to_transaction_buy():
    transaction = _OPTransaction(
        book_date=date(2025, 8, 7),
        value_date=date(2025, 8, 7),
        amount_in_euro=-625.7,
        category="700",
        explanation="NOSTO",
        message="O:PFE US /30",
    )

    result = transaction.to_transaction()

    assert result == Trade(
        security_id="PFE",
        action=Action.BUY,
        share_amount=30,
        date=date(2025, 8, 7),
        money=Decimal("-625.7"),
    )


def test_to_transaction_sell():
    transaction = _OPTransaction(
        book_date=date(2026, 1, 14),
        value_date=date(2026, 1, 14),
        amount_in_euro=612.0,
        category="700",
        explanation="PANO",
        message="M:MRNA /20 578876374313",
    )

    result = transaction.to_transaction()

    assert result == Trade(
        security_id="MRNA",
        action=Action.SELL,
        share_amount=20,
        date=date(2026, 1, 14),
        money=Decimal("612.0"),
    )


def test_to_transaction_dividend():
    transaction = _OPTransaction(
        book_date=date(2025, 8, 27),
        value_date=date(2025, 8, 27),
        amount_in_euro=5.89,
        category="710",
        explanation="ARVOPAPERIT",
        message=(
            " OP Säilytys Oy                     SIRIUSXM HOLDINGS                  "
            "US8299331004                       Osinkotuotto                       "
            "Osinko        0,27          USD/KplOmistettu määrä             30Kpl  "
            "Tuoton määrä                8,10USDLähdevero  US15,0   %       1,22USD"
            "Veroero                     0,01USDVal.kurssi                1,1687"
        ),
    )

    result = transaction.to_transaction()

    assert result == Dividend(
        security_id="US8299331004",
        share_amount=30,
        date=date(2025, 8, 27),
        money=Decimal("5.89"),
    )

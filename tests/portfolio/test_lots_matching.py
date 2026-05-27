import io
from datetime import date

import pandas as pd

from investment.portfolio.lots_matching import fifo_lots_matching, Lot, Action, RealizedLots, _to_lot, \
    UnrealizedLots


def make_row(viesti: str, selitys: str, maara: str, kirjauspaiva: str) -> pd.Series:
    csv = f"Viesti;Selitys;Määrä EUROA;Kirjauspäivä\n{viesti};{selitys};{maara};{kirjauspaiva}"
    return pd.read_csv(io.StringIO(csv), sep=";", decimal=",").iloc[0]


def test_to_lot_buy():
    row = make_row(" O:NOVO B /20", "NOSTO", "-690,83", "25.02.2026")
    company_identifier, lot = _to_lot(row)
    assert company_identifier == "NOVO B"
    assert lot == Lot(date=date(2026, 2, 25), action=Action.BUY, share_amount=20, value_in_cent=69083)


def test_to_lot_sell():
    row = make_row(" M:MRNA /20 578876374313", "PANO", "689,71", "14.01.2026")
    company_identifier, lot = _to_lot(row)
    assert company_identifier == "MRNA"
    assert lot == Lot(date=date(2026, 1, 14), action=Action.SELL, share_amount=20, value_in_cent=68971)


def test_to_lot_value_in_cent():
    row = make_row(" O:MANTA /80", "NOSTO", "-551,40", "02.03.2026")
    company_identifier, lot = _to_lot(row)
    assert company_identifier == "MANTA"
    assert lot == Lot(date=date(2026, 3, 2), action=Action.BUY, share_amount=80, value_in_cent=55140)


def test_fifo_one_lot_removed():
    buy1 = Lot(date=date(2026, 1, 13), action=Action.BUY, share_amount=20, value_in_cent=57596)
    buy2 = Lot(date=date(2026, 1, 13), action=Action.BUY, share_amount=20, value_in_cent=57468)
    sell = Lot(date=date(2026, 1, 14), action=Action.SELL, share_amount=20, value_in_cent=61200)

    realized_lots, unrealized_lots = fifo_lots_matching([buy1, buy2, sell])

    assert realized_lots == [RealizedLots([sell, buy1])]
    assert unrealized_lots == UnrealizedLots([buy2])
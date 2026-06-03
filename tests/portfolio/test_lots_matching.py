import io
from datetime import date

import pandas as pd

from investment.portfolio.lots_matching import match_lots_in_fifo, Realized, _to_lot, \
    BuyLot, SellLot, Unrealized


def make_row(viesti: str, selitys: str, maara: str, kirjauspaiva: str) -> pd.Series:
    csv = f"Viesti;Selitys;Määrä EUROA;Kirjauspäivä\n{viesti};{selitys};{maara};{kirjauspaiva}"
    return pd.read_csv(io.StringIO(csv), sep=";", decimal=",").iloc[0]


def test_to_lot_buy():
    row = make_row(" O:NOVO B /20", "NOSTO", "-690,83", "25.02.2026")
    company_identifier, lot = _to_lot(row)
    assert company_identifier == "NOVO B"
    assert lot == BuyLot(date=date(2026, 2, 25), share_amount=20, value_in_cent=69083)


def test_to_lot_sell():
    row = make_row(" M:MRNA /20 578876374313", "PANO", "689,71", "14.01.2026")
    company_identifier, lot = _to_lot(row)
    assert company_identifier == "MRNA"
    assert lot == SellLot(date=date(2026, 1, 14), share_amount=20, value_in_cent=68971)


def test_to_lot_value_in_cent():
    row = make_row(" O:MANTA /80", "NOSTO", "-551,40", "02.03.2026")
    company_identifier, lot = _to_lot(row)
    assert company_identifier == "MANTA"
    assert lot == BuyLot(date=date(2026, 3, 2), share_amount=80, value_in_cent=55140)


def test_fifo_one_lot_removed():
    buy1 = BuyLot(date=date(2026, 1, 13), share_amount=20, value_in_cent=57596)
    buy2 = BuyLot(date=date(2026, 1, 13), share_amount=20, value_in_cent=57468)
    sell = SellLot(date=date(2026, 1, 14), share_amount=20, value_in_cent=61200)

    realized, unrealized = match_lots_in_fifo([buy1, buy2, sell])

    assert realized == Realized([Realized.LotsGroup(sell, [buy1])])
    assert unrealized == Unrealized([buy2])

def test_fifo_multiple_lots_removed():
    buy1 = BuyLot(date=date(2025, 8, 8), share_amount=50, value_in_cent=38900)
    buy2 = BuyLot(date=date(2025, 8, 25), share_amount=70, value_in_cent=51300)
    buy3 = BuyLot(date=date(2025, 11, 26), share_amount=90, value_in_cent=58500)
    sell = SellLot(date=date(2025, 5, 7), share_amount=140, value_in_cent=187400)

    realized, unrealized = match_lots_in_fifo([buy1, buy2, buy3, sell])
    last_removed_lot_cost_in_cent = (58500 * 20) // 90
    assert realized == Realized([Realized.LotsGroup(sell, [buy1, buy2, BuyLot(date=date(2025, 11, 26), share_amount=20, value_in_cent=last_removed_lot_cost_in_cent)])])
    assert unrealized == Unrealized([BuyLot(date=date(2025, 11, 26), share_amount=70, value_in_cent=58500-last_removed_lot_cost_in_cent)])
from datetime import date
from pathlib import Path

import pytest

from investment.portfolio.calculation import time_weighted_return_by_period, NetAsset, SubPeriodReturn
from investment.portfolio.lots_matching import Action, Lot
from investment.text_io import extract_csv

INPUT_DIR = Path(__file__).parent / "input"


@pytest.fixture(scope="module")
def sub_period_returns():
    transactions = extract_csv(path=str(INPUT_DIR), sep=";", encoding="latin-1")
    return time_weighted_return_by_period(transactions)


@pytest.mark.integration
def test_subperiods_count(sub_period_returns):
    assert len(sub_period_returns) == 6


@pytest.mark.integration
@pytest.mark.parametrize("i,expected", [
    (0, SubPeriodReturn(
        beginning_net_asset=NetAsset(date=date(2025, 7, 21), cash_in_cent=700000, holdings_map={}),
        ending_net_asset=NetAsset(date=date(2025, 7, 31), cash_in_cent=610001, holdings_map={}))),
    (1, SubPeriodReturn(
        beginning_net_asset=NetAsset(date=date(2025, 7, 31), cash_in_cent=605001, holdings_map={}),
        ending_net_asset=NetAsset(date=date(2025, 8,  3), cash_in_cent=605001, holdings_map={}))),
    (2, SubPeriodReturn(
        beginning_net_asset=NetAsset(date=date(2025, 8, 3), cash_in_cent=603452, holdings_map={}),
        ending_net_asset=NetAsset(date=date(2025, 8, 26), cash_in_cent=395659, holdings_map={
            'PFE US': [Lot(date=date(2025, 8, 7), action=Action.BUY, share_amount=30, value_in_cent=62570)],
            'SIRI': [Lot(date=date(2025, 8, 7), action=Action.BUY, share_amount=30, value_in_cent=54919)],
            'SAGCV': [Lot(date=date(2025, 8, 8), action=Action.BUY, share_amount=50, value_in_cent=38900),
                      Lot(date=date(2025, 8, 25), action=Action.BUY, share_amount=70, value_in_cent=51300)]
        })
    )),
    (3, SubPeriodReturn(
        beginning_net_asset=NetAsset(date=date(2025, 8, 26), cash_in_cent=386874, holdings_map={
            'PFE US': [Lot(date=date(2025, 8, 7), action=Action.BUY, share_amount=30, value_in_cent=62570)],
            'SIRI': [Lot(date=date(2025, 8, 7), action=Action.BUY, share_amount=30, value_in_cent=54919)],
            'SAGCV': [Lot(date=date(2025, 8, 8), action=Action.BUY, share_amount=50, value_in_cent=38900),
                      Lot(date=date(2025, 8, 25), action=Action.BUY, share_amount=70, value_in_cent=51300)]
        }),
        ending_net_asset=NetAsset(date=date(2025, 9,  1), cash_in_cent=387463, holdings_map={
            'PFE US': [Lot(date=date(2025, 8, 7), action=Action.BUY, share_amount=30, value_in_cent=62570)],
            'SIRI': [Lot(date=date(2025, 8, 7), action=Action.BUY, share_amount=30, value_in_cent=54919)],
            'SAGCV': [Lot(date=date(2025, 8, 8), action=Action.BUY, share_amount=50, value_in_cent=38900),
                      Lot(date=date(2025, 8, 25), action=Action.BUY, share_amount=70, value_in_cent=51300)]
        })
    )),
    (4, SubPeriodReturn(
        beginning_net_asset=NetAsset(date=date(2025, 9,  1), cash_in_cent=1187463, holdings_map={
            'PFE US': [Lot(date=date(2025, 8, 7), action=Action.BUY, share_amount=30, value_in_cent=62570)],
            'SIRI': [Lot(date=date(2025, 8, 7), action=Action.BUY, share_amount=30, value_in_cent=54919)],
            'SAGCV': [Lot(date=date(2025, 8, 8), action=Action.BUY, share_amount=50, value_in_cent=38900),
                      Lot(date=date(2025, 8, 25), action=Action.BUY, share_amount=70, value_in_cent=51300)]
        }),
        ending_net_asset=NetAsset(date=date(2026, 1, 14), cash_in_cent=15701, holdings_map={
            'PFE US': [Lot(date=date(2025, 8, 7), action=Action.BUY, share_amount=30, value_in_cent=62570)],
            'SIRI': [Lot(date=date(2025, 8, 7), action=Action.BUY, share_amount=30, value_in_cent=54919),
                     Lot(date=date(2025, 12, 29), action=Action.BUY, share_amount=25, value_in_cent=44327)],
            'SAGCV': [Lot(date=date(2025, 8, 8), action=Action.BUY, share_amount=50, value_in_cent=38900),
                      Lot(date=date(2025, 8, 25), action=Action.BUY, share_amount=70, value_in_cent=51300),
                      Lot(date=date(2025, 11, 3), action=Action.BUY, share_amount=80, value_in_cent=55300),
                      Lot(date=date(2025, 11, 26), action=Action.BUY, share_amount=90, value_in_cent=58500)],
            'STZ.N': [Lot(date=date(2025, 9, 3), action=Action.BUY, share_amount=4, value_in_cent=52566),
                      Lot(date=date(2025, 9, 15), action=Action.BUY, share_amount=5, value_in_cent=61285),
                      Lot(date=date(2025, 9, 18), action=Action.BUY, share_amount=6, value_in_cent=69197),
                      Lot(date=date(2025, 11, 3), action=Action.BUY, share_amount=6, value_in_cent=67387)],
            'BNTX US': [Lot(date=date(2025, 9, 3), action=Action.BUY, share_amount=4, value_in_cent=34982)],
            'OXY US': [Lot(date=date(2025, 10, 7), action=Action.BUY, share_amount=20, value_in_cent=77186),
                       Lot(date=date(2025, 10, 14), action=Action.BUY, share_amount=30, value_in_cent=113886),
                       Lot(date=date(2025, 10, 17), action=Action.BUY, share_amount=25, value_in_cent=90101)],
            'VZ US': [Lot(date=date(2025, 10, 23), action=Action.BUY, share_amount=15, value_in_cent=52025)],
            'NOVO B': [Lot(date=date(2025, 11, 11), action=Action.BUY, share_amount=12, value_in_cent=49253),
                       Lot(date=date(2025, 11, 26), action=Action.BUY, share_amount=20, value_in_cent=74484)],
            'T CN': [Lot(date=date(2025, 12, 2), action=Action.BUY, share_amount=50, value_in_cent=56775)],
            'PDD US': [Lot(date=date(2025, 12, 18), action=Action.BUY, share_amount=4, value_in_cent=36738)],
            'GIS US': [Lot(date=date(2026, 1, 5), action=Action.BUY, share_amount=10, value_in_cent=39893)],
            'MRNA': [Lot(date=date(2026, 1, 13), action=Action.BUY, share_amount=20, value_in_cent=57596),
                     Lot(date=date(2026, 1, 13), action=Action.BUY, share_amount=20, value_in_cent=57467)],
        }))),
    (5, SubPeriodReturn(
        beginning_net_asset=NetAsset(date=date(2026, 1, 14), cash_in_cent=115701, holdings_map={
            'PFE US': [Lot(date=date(2025, 8, 7), action=Action.BUY, share_amount=30, value_in_cent=62570)],
            'SIRI': [Lot(date=date(2025, 8, 7), action=Action.BUY, share_amount=30, value_in_cent=54919),
                     Lot(date=date(2025, 12, 29), action=Action.BUY, share_amount=25, value_in_cent=44327)],
            'SAGCV': [Lot(date=date(2025, 8, 8), action=Action.BUY, share_amount=50, value_in_cent=38900),
                      Lot(date=date(2025, 8, 25), action=Action.BUY, share_amount=70, value_in_cent=51300),
                      Lot(date=date(2025, 11, 3), action=Action.BUY, share_amount=80, value_in_cent=55300),
                      Lot(date=date(2025, 11, 26), action=Action.BUY, share_amount=90, value_in_cent=58500)],
            'STZ.N': [Lot(date=date(2025, 9, 3), action=Action.BUY, share_amount=4, value_in_cent=52566),
                      Lot(date=date(2025, 9, 15), action=Action.BUY, share_amount=5, value_in_cent=61285),
                      Lot(date=date(2025, 9, 18), action=Action.BUY, share_amount=6, value_in_cent=69197),
                      Lot(date=date(2025, 11, 3), action=Action.BUY, share_amount=6, value_in_cent=67387)],
            'BNTX US': [Lot(date=date(2025, 9, 3), action=Action.BUY, share_amount=4, value_in_cent=34982)],
            'OXY US': [Lot(date=date(2025, 10, 7), action=Action.BUY, share_amount=20, value_in_cent=77186),
                       Lot(date=date(2025, 10, 14), action=Action.BUY, share_amount=30, value_in_cent=113886),
                       Lot(date=date(2025, 10, 17), action=Action.BUY, share_amount=25, value_in_cent=90101)],
            'VZ US': [Lot(date=date(2025, 10, 23), action=Action.BUY, share_amount=15, value_in_cent=52025)],
            'NOVO B': [Lot(date=date(2025, 11, 11), action=Action.BUY, share_amount=12, value_in_cent=49253),
                       Lot(date=date(2025, 11, 26), action=Action.BUY, share_amount=20, value_in_cent=74484)],
            'T CN': [Lot(date=date(2025, 12, 2), action=Action.BUY, share_amount=50, value_in_cent=56775)],
            'PDD US': [Lot(date=date(2025, 12, 18), action=Action.BUY, share_amount=4, value_in_cent=36738)],
            'GIS US': [Lot(date=date(2026, 1, 5), action=Action.BUY, share_amount=10, value_in_cent=39893)],
            'MRNA': [Lot(date=date(2026, 1, 13), action=Action.BUY, share_amount=20, value_in_cent=57596),
                     Lot(date=date(2026, 1, 13), action=Action.BUY, share_amount=20, value_in_cent=57467)],
        }),
        ending_net_asset=NetAsset(date=date(2026, 1, 30), cash_in_cent=70539, holdings_map={
            'PFE US': [Lot(date=date(2025, 8, 7), action=Action.BUY, share_amount=30, value_in_cent=62570)],
            'SIRI': [Lot(date=date(2025, 8, 7), action=Action.BUY, share_amount=30, value_in_cent=54919),
                     Lot(date=date(2025, 12, 29), action=Action.BUY, share_amount=25, value_in_cent=44327),
                     Lot(date=date(2026, 1, 30), action=Action.BUY, share_amount=35, value_in_cent=59561)],
            'SAGCV': [Lot(date=date(2025, 8, 8), action=Action.BUY, share_amount=50, value_in_cent=38900),
                      Lot(date=date(2025, 8, 25), action=Action.BUY, share_amount=70, value_in_cent=51300),
                      Lot(date=date(2025, 11, 3), action=Action.BUY, share_amount=80, value_in_cent=55300),
                      Lot(date=date(2025, 11, 26), action=Action.BUY, share_amount=90, value_in_cent=58500)],
            'STZ.N': [Lot(date=date(2025, 9, 3), action=Action.BUY, share_amount=4, value_in_cent=52566),
                      Lot(date=date(2025, 9, 15), action=Action.BUY, share_amount=5, value_in_cent=61285),
                      Lot(date=date(2025, 9, 18), action=Action.BUY, share_amount=6, value_in_cent=69197),
                      Lot(date=date(2025, 11, 3), action=Action.BUY, share_amount=6, value_in_cent=67387)],
            'BNTX US': [Lot(date=date(2025, 9, 3), action=Action.BUY, share_amount=4, value_in_cent=34982)],
            'OXY US': [Lot(date=date(2025, 10, 7), action=Action.BUY, share_amount=20, value_in_cent=77186),
                       Lot(date=date(2025, 10, 14), action=Action.BUY, share_amount=30, value_in_cent=113886),
                       Lot(date=date(2025, 10, 17), action=Action.BUY, share_amount=25, value_in_cent=90101)],
            'VZ US': [Lot(date=date(2025, 10, 23), action=Action.BUY, share_amount=15, value_in_cent=52025)],
            'NOVO B': [Lot(date=date(2025, 11, 11), action=Action.BUY, share_amount=12, value_in_cent=49253),
                       Lot(date=date(2025, 11, 26), action=Action.BUY, share_amount=20, value_in_cent=74484)],
            'T CN': [Lot(date=date(2025, 12, 2), action=Action.BUY, share_amount=50, value_in_cent=56775)],
            'PDD US': [Lot(date=date(2025, 12, 18), action=Action.BUY, share_amount=4, value_in_cent=36738)],
            'GIS US': [Lot(date=date(2026, 1, 5), action=Action.BUY, share_amount=10, value_in_cent=39893)],
            'MRNA': [Lot(date=date(2026, 1, 13), action=Action.BUY, share_amount=20, value_in_cent=57467)],
            'UNH US': [Lot(date=date(2026, 1, 28), action=Action.BUY, share_amount=2, value_in_cent=48108)],
        }))),
])
def test_subperiod_dates(sub_period_returns, i, expected):
    assert sub_period_returns[i] == expected

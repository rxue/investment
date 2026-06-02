from datetime import date
from pathlib import Path

import pytest

from investment.portfolio.twr import time_weighted_return_by_period, NetAsset, SubPeriodReturn
from investment.portfolio.lots_matching import BuyLot
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
        beginning_net_asset=NetAsset(date=date(2025, 7, 21), cash_in_cent=700000, unrealized_lots_map={}),
        ending_net_asset=NetAsset(date=date(2025, 7, 31), cash_in_cent=610001, unrealized_lots_map={}))),
    (1, SubPeriodReturn(
        beginning_net_asset=NetAsset(date=date(2025, 7, 31), cash_in_cent=605001, unrealized_lots_map={}),
        ending_net_asset=NetAsset(date=date(2025, 8,  3), cash_in_cent=605001, unrealized_lots_map={}))),
    (2, SubPeriodReturn(
        beginning_net_asset=NetAsset(date=date(2025, 8, 3), cash_in_cent=603452, unrealized_lots_map={}),
        ending_net_asset=NetAsset(date=date(2025, 8, 26), cash_in_cent=395659, unrealized_lots_map={
            'PFE US': [BuyLot(date=date(2025, 8, 7), share_amount=30, value_in_cent=62570)],
            'SIRI': [BuyLot(date=date(2025, 8, 7), share_amount=30, value_in_cent=54919)],
            'SAGCV': [BuyLot(date=date(2025, 8, 8), share_amount=50, value_in_cent=38900),
                      BuyLot(date=date(2025, 8, 25), share_amount=70, value_in_cent=51300)]
        })
    )),
    (3, SubPeriodReturn(
        beginning_net_asset=NetAsset(date=date(2025, 8, 26), cash_in_cent=386874, unrealized_lots_map={
            'PFE US': [BuyLot(date=date(2025, 8, 7), share_amount=30, value_in_cent=62570)],
            'SIRI': [BuyLot(date=date(2025, 8, 7), share_amount=30, value_in_cent=54919)],
            'SAGCV': [BuyLot(date=date(2025, 8, 8), share_amount=50, value_in_cent=38900),
                      BuyLot(date=date(2025, 8, 25), share_amount=70, value_in_cent=51300)]
        }),
        ending_net_asset=NetAsset(date=date(2025, 9,  1), cash_in_cent=387463, unrealized_lots_map={
            'PFE US': [BuyLot(date=date(2025, 8, 7), share_amount=30, value_in_cent=62570)],
            'SIRI': [BuyLot(date=date(2025, 8, 7), share_amount=30, value_in_cent=54919)],
            'SAGCV': [BuyLot(date=date(2025, 8, 8), share_amount=50, value_in_cent=38900),
                      BuyLot(date=date(2025, 8, 25), share_amount=70, value_in_cent=51300)]
        })
    )),
    (4, SubPeriodReturn(
        beginning_net_asset=NetAsset(date=date(2025, 9,  1), cash_in_cent=1187463, unrealized_lots_map={
            'PFE US': [BuyLot(date=date(2025, 8, 7), share_amount=30, value_in_cent=62570)],
            'SIRI': [BuyLot(date=date(2025, 8, 7), share_amount=30, value_in_cent=54919)],
            'SAGCV': [BuyLot(date=date(2025, 8, 8), share_amount=50, value_in_cent=38900),
                      BuyLot(date=date(2025, 8, 25), share_amount=70, value_in_cent=51300)]
        }),
        ending_net_asset=NetAsset(date=date(2026, 1, 14), cash_in_cent=15701, unrealized_lots_map={
            'PFE US': [BuyLot(date=date(2025, 8, 7), share_amount=30, value_in_cent=62570)],
            'SIRI': [BuyLot(date=date(2025, 8, 7), share_amount=30, value_in_cent=54919),
                     BuyLot(date=date(2025, 12, 29), share_amount=25, value_in_cent=44327)],
            'SAGCV': [BuyLot(date=date(2025, 8, 8), share_amount=50, value_in_cent=38900),
                      BuyLot(date=date(2025, 8, 25), share_amount=70, value_in_cent=51300),
                      BuyLot(date=date(2025, 11, 3), share_amount=80, value_in_cent=55300),
                      BuyLot(date=date(2025, 11, 26), share_amount=90, value_in_cent=58500)],
            'STZ.N': [BuyLot(date=date(2025, 9, 3), share_amount=4, value_in_cent=52566),
                      BuyLot(date=date(2025, 9, 15), share_amount=5, value_in_cent=61285),
                      BuyLot(date=date(2025, 9, 18), share_amount=6, value_in_cent=69197),
                      BuyLot(date=date(2025, 11, 3), share_amount=6, value_in_cent=67387)],
            'BNTX US': [BuyLot(date=date(2025, 9, 3), share_amount=4, value_in_cent=34982)],
            'OXY US': [BuyLot(date=date(2025, 10, 7), share_amount=20, value_in_cent=77186),
                       BuyLot(date=date(2025, 10, 14), share_amount=30, value_in_cent=113886),
                       BuyLot(date=date(2025, 10, 17), share_amount=25, value_in_cent=90101)],
            'VZ US': [BuyLot(date=date(2025, 10, 23), share_amount=15, value_in_cent=52025)],
            'NOVO B': [BuyLot(date=date(2025, 11, 11), share_amount=12, value_in_cent=49253),
                       BuyLot(date=date(2025, 11, 26), share_amount=20, value_in_cent=74484)],
            'T CN': [BuyLot(date=date(2025, 12, 2), share_amount=50, value_in_cent=56775)],
            'PDD US': [BuyLot(date=date(2025, 12, 18), share_amount=4, value_in_cent=36738)],
            'GIS US': [BuyLot(date=date(2026, 1, 5), share_amount=10, value_in_cent=39893)],
            'MRNA': [BuyLot(date=date(2026, 1, 13), share_amount=20, value_in_cent=57596),
                     BuyLot(date=date(2026, 1, 13), share_amount=20, value_in_cent=57467)],
        }))),
    (5, SubPeriodReturn(
        beginning_net_asset=NetAsset(date=date(2026, 1, 14), cash_in_cent=115701, unrealized_lots_map={
            'PFE US': [BuyLot(date=date(2025, 8, 7), share_amount=30, value_in_cent=62570)],
            'SIRI': [BuyLot(date=date(2025, 8, 7), share_amount=30, value_in_cent=54919),
                     BuyLot(date=date(2025, 12, 29), share_amount=25, value_in_cent=44327)],
            'SAGCV': [BuyLot(date=date(2025, 8, 8), share_amount=50, value_in_cent=38900),
                      BuyLot(date=date(2025, 8, 25), share_amount=70, value_in_cent=51300),
                      BuyLot(date=date(2025, 11, 3), share_amount=80, value_in_cent=55300),
                      BuyLot(date=date(2025, 11, 26), share_amount=90, value_in_cent=58500)],
            'STZ.N': [BuyLot(date=date(2025, 9, 3), share_amount=4, value_in_cent=52566),
                      BuyLot(date=date(2025, 9, 15), share_amount=5, value_in_cent=61285),
                      BuyLot(date=date(2025, 9, 18), share_amount=6, value_in_cent=69197),
                      BuyLot(date=date(2025, 11, 3), share_amount=6, value_in_cent=67387)],
            'BNTX US': [BuyLot(date=date(2025, 9, 3), share_amount=4, value_in_cent=34982)],
            'OXY US': [BuyLot(date=date(2025, 10, 7), share_amount=20, value_in_cent=77186),
                       BuyLot(date=date(2025, 10, 14), share_amount=30, value_in_cent=113886),
                       BuyLot(date=date(2025, 10, 17), share_amount=25, value_in_cent=90101)],
            'VZ US': [BuyLot(date=date(2025, 10, 23), share_amount=15, value_in_cent=52025)],
            'NOVO B': [BuyLot(date=date(2025, 11, 11), share_amount=12, value_in_cent=49253),
                       BuyLot(date=date(2025, 11, 26), share_amount=20, value_in_cent=74484)],
            'T CN': [BuyLot(date=date(2025, 12, 2), share_amount=50, value_in_cent=56775)],
            'PDD US': [BuyLot(date=date(2025, 12, 18), share_amount=4, value_in_cent=36738)],
            'GIS US': [BuyLot(date=date(2026, 1, 5), share_amount=10, value_in_cent=39893)],
            'MRNA': [BuyLot(date=date(2026, 1, 13), share_amount=20, value_in_cent=57596),
                     BuyLot(date=date(2026, 1, 13), share_amount=20, value_in_cent=57467)],
        }),
        ending_net_asset=NetAsset(date=date(2026, 1, 30), cash_in_cent=70539, unrealized_lots_map={
            'PFE US': [BuyLot(date=date(2025, 8, 7), share_amount=30, value_in_cent=62570)],
            'SIRI': [BuyLot(date=date(2025, 8, 7), share_amount=30, value_in_cent=54919),
                     BuyLot(date=date(2025, 12, 29), share_amount=25, value_in_cent=44327),
                     BuyLot(date=date(2026, 1, 30), share_amount=35, value_in_cent=59561)],
            'SAGCV': [BuyLot(date=date(2025, 8, 8), share_amount=50, value_in_cent=38900),
                      BuyLot(date=date(2025, 8, 25), share_amount=70, value_in_cent=51300),
                      BuyLot(date=date(2025, 11, 3), share_amount=80, value_in_cent=55300),
                      BuyLot(date=date(2025, 11, 26), share_amount=90, value_in_cent=58500)],
            'STZ.N': [BuyLot(date=date(2025, 9, 3), share_amount=4, value_in_cent=52566),
                      BuyLot(date=date(2025, 9, 15), share_amount=5, value_in_cent=61285),
                      BuyLot(date=date(2025, 9, 18), share_amount=6, value_in_cent=69197),
                      BuyLot(date=date(2025, 11, 3), share_amount=6, value_in_cent=67387)],
            'BNTX US': [BuyLot(date=date(2025, 9, 3), share_amount=4, value_in_cent=34982)],
            'OXY US': [BuyLot(date=date(2025, 10, 7), share_amount=20, value_in_cent=77186),
                       BuyLot(date=date(2025, 10, 14), share_amount=30, value_in_cent=113886),
                       BuyLot(date=date(2025, 10, 17), share_amount=25, value_in_cent=90101)],
            'VZ US': [BuyLot(date=date(2025, 10, 23), share_amount=15, value_in_cent=52025)],
            'NOVO B': [BuyLot(date=date(2025, 11, 11), share_amount=12, value_in_cent=49253),
                       BuyLot(date=date(2025, 11, 26), share_amount=20, value_in_cent=74484)],
            'T CN': [BuyLot(date=date(2025, 12, 2), share_amount=50, value_in_cent=56775)],
            'PDD US': [BuyLot(date=date(2025, 12, 18), share_amount=4, value_in_cent=36738)],
            'GIS US': [BuyLot(date=date(2026, 1, 5), share_amount=10, value_in_cent=39893)],
            'MRNA': [BuyLot(date=date(2026, 1, 13), share_amount=20, value_in_cent=57467)],
            'UNH US': [BuyLot(date=date(2026, 1, 28), share_amount=2, value_in_cent=48108)],
        }))),
])
def test_subperiod_dates(sub_period_returns, i, expected):
    assert sub_period_returns[i] == expected

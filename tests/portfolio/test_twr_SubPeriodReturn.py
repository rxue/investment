from datetime import date

from investment.portfolio.twr import SubPeriodReturn, NetAsset


def test_value_no_holdings():
    spr = SubPeriodReturn(
        beginning_net_asset=NetAsset(date=date(2025, 1, 1), cash_in_cent=100000, holdings_map={}),
        ending_net_asset=NetAsset(date=date(2025, 2, 1), cash_in_cent=110000, holdings_map={}),
    )
    assert spr.value() == 0

from datetime import date
from statistics import geometric_mean
from typing import NamedTuple

import pandas as pd

from investment.data_fetch.company_fetcher import find_yahoo_symbol
from investment.data_fetch import yfinance_fetcher
from investment.portfolio.lots_matching import BuyLot, \
    group_match_lots_in_fifo, MatchingResult
from investment.portfolio.util import make_df
from investment.util import to_date

class NetAsset(NamedTuple):
    date:date
    cash_in_cent:int
    unrealized_lots_map:dict[str,list[BuyLot]]
    def _equity_market_value_in_cent(self) -> int:
        market_value = 0
        for company_symbol, unrealized_lots in self.unrealized_lots_map.items():
            yahoo_company_symbol = find_yahoo_symbol(company_symbol)
            price_in_euro = yfinance_fetcher.get_close_price(yahoo_company_symbol, self.date).in_euro()
            market_value += int(round(price_in_euro * sum([l.share_amount for l in unrealized_lots]) * 100))
        return market_value
    def total_value_in_cent(self):
        return self._equity_market_value_in_cent() + self.cash_in_cent
    def has_holdings(self):
        return len(self.unrealized_lots_map) > 0

class SubPeriodReturn(NamedTuple):
    beginning_net_asset:NetAsset
    ending_net_asset:NetAsset
    def value(self) -> float:
        if (not self.beginning_net_asset.has_holdings()) and (not self.ending_net_asset.has_holdings()):
            return 0
        return (self.ending_net_asset.total_value_in_cent() - self.beginning_net_asset.total_value_in_cent())/self.beginning_net_asset.total_value_in_cent()


class SubPeriodReturnCalculator:
    def __init__(self, previous_ending_net_asset:NetAsset, transactions: pd.DataFrame, exclusive_end_date: date):
        self.previous_ending_net_asset = previous_ending_net_asset
        self.transactions = transactions
        self.exclusive_end_date = exclusive_end_date
    def start_date(self):
        return to_date(self.transactions.iloc[0]["Arvopäivä"])
    def calculate_return(self) -> SubPeriodReturn:
        previous_unrealized_lots_map = self.previous_ending_net_asset.unrealized_lots_map if self.previous_ending_net_asset is not None else {}
        company_symbol_to_unrealized_lots:dict[str,MatchingResult]= group_match_lots_in_fifo(self.transactions, previous_unrealized_lots_map)
        previous_ending_cash_in_cent = 0 if self.previous_ending_net_asset is None else self.previous_ending_net_asset.cash_in_cent
        beginning_net_asset = NetAsset(
            date=to_date(self.transactions.iloc[0]["Arvopäivä"]),
            cash_in_cent=previous_ending_cash_in_cent + int(self.transactions.iloc[0]["Määrä EUROA"] * 100),
            unrealized_lots_map={} if self.previous_ending_net_asset is None else self.previous_ending_net_asset.unrealized_lots_map
        )
        ending_net_asset = NetAsset(
            date=self.exclusive_end_date,
            cash_in_cent=previous_ending_cash_in_cent + int(self.transactions["Määrä EUROA"].sum() * 100),
            unrealized_lots_map={s:matching_result.unrealized.buy_lots for s, matching_result in company_symbol_to_unrealized_lots.items()}
        )
        return SubPeriodReturn(beginning_net_asset=beginning_net_asset, ending_net_asset=ending_net_asset)



def _is_withdrawal(row: pd.Series) -> bool:
    return row["Selitys"] == "TILISIIRTO" and row["Määrä EUROA"] < 0 and not row["Viesti"].strip().startswith("O:")
def _is_deposit(row: pd.Series) -> bool:
    return row["Laji"] == 710 and row["Selitys"].strip() == "TILISIIRTO"

def is_external_cashflow(row: pd.Series) -> bool:
    return _is_withdrawal(row) or _is_deposit(row)

def divide_transactions_by_period(data: pd.DataFrame) -> list[pd.DataFrame]:
    result = []
    current_subperiod_transactions = []
    found_external_cashflow_on = None
    for i, row in data.iterrows():
        value_date = to_date(row["Arvopäivä"])
        if i > 0 and found_external_cashflow_on is None and is_external_cashflow(row):
            found_external_cashflow_on = value_date
        if found_external_cashflow_on is not None and value_date > found_external_cashflow_on and is_external_cashflow(row):
            result.append(make_df(*current_subperiod_transactions))
            current_subperiod_transactions = [row]
        else:
            current_subperiod_transactions.append(row)

    if len(current_subperiod_transactions) > 0:
        result.append(make_df(*current_subperiod_transactions))
    return result

def time_weighted_return_by_period(transactions: pd.DataFrame) -> list[SubPeriodReturn]:
    result = []
    previous_subperiod_return = None
    transactions_by_period = divide_transactions_by_period(transactions)
    for i, sub_period_df in enumerate(transactions_by_period):
        if i == len(transactions_by_period) -1:
            end_date = to_date(sub_period_df.iloc[-1]["Arvopäivä"])
        else:
            end_date = to_date(transactions_by_period[i+1].iloc[0]["Arvopäivä"])
        calculator = SubPeriodReturnCalculator(
            previous_subperiod_return.ending_net_asset if previous_subperiod_return is not None else None,
            sub_period_df, end_date)
        result += [calculator.calculate_return()]
        previous_subperiod_return = result[-1]
    return result

def twr(transactions: pd.DataFrame) -> float:
    sub_period_returns = time_weighted_return_by_period(transactions)
    growth_factors = [1 + r.value() for r in sub_period_returns]
    n = len(growth_factors)
    return geometric_mean(growth_factors) ** n - 1



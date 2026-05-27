import sys, re
from datetime import date, datetime
from statistics import geometric_mean
from typing import NamedTuple

import pandas as pd

from investment.company import find_company_by
from investment.market_quote import yfinance_fetcher
from investment.portfolio.lots_matching import Lot, fifo_lots_matching, to_lots_by_company_symbol
from investment.portfolio.transaction_filters import find_all_tradings
from investment.portfolio.util import make_df
from investment.text_io import extract_csv
from investment.util import to_date

class NetAsset(NamedTuple):
    date:date
    cash_in_cent:int
    holdings_map:dict[str,list[Lot]]
    def _equity_market_value_in_cent(self) -> int:
        market_value = 0
        for company_symbol, unrealized_lots in self.holdings_map.items():
            yahoo_company_symbol = find_company_by(company_symbol).yahoo_symbol
            price_in_euro = yfinance_fetcher.get_close_price(yahoo_company_symbol, self.date).in_euro()
            market_value += int(round(price_in_euro * sum([l.share_amount for l in unrealized_lots]) * 100))
        return market_value
    def total_value_in_cent(self):
        return self._equity_market_value_in_cent() + self.cash_in_cent

class SubPeriodReturn(NamedTuple):
    beginning_net_asset:NetAsset
    ending_net_asset:NetAsset
    def value(self) -> float:
        print(f"ending net asset value: {self.ending_net_asset.total_value_in_cent()/100}")
        return (self.ending_net_asset.total_value_in_cent() - self.beginning_net_asset.total_value_in_cent())/self.beginning_net_asset.total_value_in_cent()


class SubPeriodReturnCalculator:
    def __init__(self, previous_ending_net_asset:NetAsset, transactions: pd.DataFrame, exclusive_end_date: date):
        self.previous_ending_net_asset = previous_ending_net_asset
        self.transactions = transactions
        self.exclusive_end_date = exclusive_end_date
    def start_date(self):
        return to_date(self.transactions.iloc[0]["Arvopäivä"])
    def calculate_return(self) -> SubPeriodReturn:

        trading_transactions_df = find_all_tradings(self.transactions)
        previous_holdings_map = self.previous_ending_net_asset.holdings_map if self.previous_ending_net_asset is not None else {}
        company_symbol_to_unrealized_lots:dict[str,list[Lot]]= dict(previous_holdings_map)
        for company_symbol, tradings in to_lots_by_company_symbol(trading_transactions_df).items():
            _, unrealized_lots = fifo_lots_matching(tradings, previous_holdings_map.get(company_symbol, []))
            company_symbol_to_unrealized_lots[company_symbol] = unrealized_lots.lots
        previous_ending_cash_in_cent = 0 if self.previous_ending_net_asset is None else self.previous_ending_net_asset.cash_in_cent
        beginning_net_asset = NetAsset(
            date=to_date(self.transactions.iloc[0]["Arvopäivä"]),
            cash_in_cent=previous_ending_cash_in_cent + int(self.transactions.iloc[0]["Määrä EUROA"] * 100),
            holdings_map={} if self.previous_ending_net_asset is None else self.previous_ending_net_asset.holdings_map
        )
        ending_net_asset = NetAsset(
            date=self.exclusive_end_date,
            cash_in_cent=previous_ending_cash_in_cent + int(self.transactions["Määrä EUROA"].sum() * 100),
            holdings_map=company_symbol_to_unrealized_lots
        )
        return SubPeriodReturn(beginning_net_asset=beginning_net_asset, ending_net_asset=ending_net_asset)



def is_withdrawal(row: pd.Series) -> bool:
    return row["Selitys"] == "TILISIIRTO" and row["Määrä EUROA"] < 0 and not row["Viesti"].strip().startswith("O:")
def is_deposit(row: pd.Series) -> bool:
    return row["Laji"] == 710 and row["Selitys"].strip() == "TILISIIRTO"

def is_external_cashflow(row: pd.Series) -> bool:
    return is_withdrawal(row) or is_deposit(row)

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

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python -m investment.portfolio.calculation <csv_dir>")
        sys.exit(1)
    df = extract_csv(path=sys.argv[1], sep=";", encoding="latin-1")
    print(f"time-weighted return: {twr(df)}")


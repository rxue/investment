from datetime import date
from typing import NamedTuple

import pandas as pd

from investment.data_fetch.company_fetcher import find_company_by
from investment.portfolio.holdings.models import Holding
from investment.portfolio.lots_matching import group_match_lots_in_fifo, Unrealized


class Period(NamedTuple):
    start_date:date
    end_date:date


def calculate_holdings_with_capital_gain_in_cent(transactions:pd.DataFrame, period:Period) -> tuple[list[Holding],dict[str,int]]:
    def transactions_by(transactions:pd.DataFrame, date:date) -> pd.DataFrame:
        return transactions[pd.to_datetime(transactions["Arvopäivä"], dayfirst=True).dt.date <= date]
    def to_holding(company_symbol:str, unrealized:Unrealized) -> Holding:
        return Holding(find_company_by(company_symbol), unrealized.position())
    matching_result_map = group_match_lots_in_fifo(transactions_by(transactions, period.end_date))
    capital_gain_map = {company_symbol:matching_result.realized.capital_gain(period.start_date) for company_symbol, matching_result in matching_result_map.items()}
    return [to_holding(company_symbol, unrealized) for company_symbol, unrealized in matching_result_map], capital_gain_map
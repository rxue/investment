from enum import Enum, auto
import re
from typing import NamedTuple, Protocol
from datetime import date, datetime

import pandas as pd

from investment.portfolio.transaction_filters import find_all_tradings


class Action(Enum):
    BUY = auto()
    SELL = auto()

class Lot(Protocol):
    date: date
    share_amount: int
    value_in_cent: int
    def action(self) -> Action: ...

class BuyLot(NamedTuple):
    date: date
    share_amount: int
    value_in_cent: int
    def action(self) -> Action:
        return Action.BUY


class SellLot(NamedTuple):
    date: date
    share_amount: int
    value_in_cent: int
    def action(self) -> Action:
        return Action.SELL

def _to_lot(row: pd.Series) -> tuple[str,Lot]:
    match = re.match(r"^\s*([OM]):(.+?)\s*/(\d+)", row["Viesti"])
    action = match.group(1)
    ticker = match.group(2).strip()
    quantity = int(match.group(3))
    trading_date = datetime.strptime(row["Kirjauspäivä"], "%d.%m.%Y").date()
    trade_price = abs(row["Määrä EUROA"])
    lot_args = dict(date=trading_date, share_amount=quantity, value_in_cent=int(trade_price * 100))
    lot = BuyLot(**lot_args) if action == "O" else SellLot(**lot_args)
    return ticker, lot

def get_trading_lots_by_company_symbol(transactions_df: pd.DataFrame) -> dict[str,list[Lot]]:
    def to_lots_by_company_symbol(tradings_df: pd.DataFrame) -> dict[str, list[Lot]]:
        result: dict[str, list[Lot]] = {}
        for _, row in tradings_df.iterrows():
            company_identifier, lots = _to_lot(row)
            result.setdefault(company_identifier, []).append(lots)
        return result
    tradings_df = find_all_tradings(transactions_df)
    return to_lots_by_company_symbol(tradings_df)

class Realized(NamedTuple):
    class LotsGroup(NamedTuple):
        sell_lot: SellLot
        buy_lots: list[BuyLot]
        def realized_gain_in_cent(self):
            return self.sell_lot.value_in_cent - sum([lot.value_in_cent if lot.action == Action.BUY else -lot.value_in_cent for lot in self.buy_lots])
        def is_on_or_after(self, date:date):
            return self.sell_lot.date >= date
    lots_groups: list[LotsGroup]
    def capital_gain(self, start_date:date):
        return sum(g.realized_gain_in_cent() if g.is_on_or_after(start_date) else 0 for g in self.lots_groups)



class Unrealized(NamedTuple):
    buy_lots: list[BuyLot]
    def position(self):
        return sum(lot.share_amount for lot in self.buy_lots)
    def cost(self) -> float:
        return sum(lot.value_in_cent for lot in self.buy_lots) / 100

class MatchingResult(NamedTuple):
    realized: Realized
    unrealized: Unrealized

def match_lots_in_fifo(tradings:list[Lot], existing_unrealized_lots:list[BuyLot]=[]) -> MatchingResult:
    remaining_fifo_lots: list[BuyLot] = list(existing_unrealized_lots)
    def dequeue(sell_lot: SellLot) -> Realized.LotsGroup:
        realized_buy_lots = []
        amount_to_dequeue = sell_lot.share_amount
        while amount_to_dequeue > 0:
            head_lot = remaining_fifo_lots.pop(0)
            if head_lot.share_amount <= amount_to_dequeue:
                amount_to_dequeue -= head_lot.share_amount
                realized_buy_lots.append(head_lot)
            else:
                new_realized_value_in_cent = (amount_to_dequeue/head_lot.share_amount)*head_lot.value_in_cent
                realized_buy_lots.append(BuyLot(date=head_lot.date, share_amount=amount_to_dequeue, value_in_cent=new_realized_value_in_cent))
                new_unrealized_share_amount = head_lot.share_amount - amount_to_dequeue
                new_unrealized_value_in_cent = head_lot.value_in_cent - new_realized_value_in_cent
                remaining_fifo_lots.append(BuyLot(date=head_lot.date, share_amount=new_unrealized_share_amount, value_in_cent=new_unrealized_value_in_cent))
                break
        return Realized.LotsGroup(sell_lot=sell_lot, buy_lots=realized_buy_lots)
    realized_lots_group_list = []
    for tr in tradings:
        if tr.action() == Action.BUY:
            remaining_fifo_lots.append(tr)
        else:
            realized_lots_group_list.append(dequeue(tr))

    return MatchingResult(Realized(realized_lots_group_list), Unrealized(remaining_fifo_lots))

def group_match_lots_in_fifo(transactions_df:pd.DataFrame, existing_unrealized_lots_map:dict[str,list[BuyLot]]={}) -> dict[str,MatchingResult]:
    def to_lots_by_company_symbol(tradings_df: pd.DataFrame) -> dict[str, list[Lot]]:
        result: dict[str, list[Lot]] = {}
        for _, row in tradings_df.iterrows():
            company_identifier, lots = _to_lot(row)
            result.setdefault(company_identifier, []).append(lots)
        return result
    tradings_df = find_all_tradings(transactions_df)
    matching_result_map = {}
    for company_symbol, lots in to_lots_by_company_symbol(tradings_df).items():
        existing_lots = existing_unrealized_lots_map.get(company_symbol)
        matching_result_map[company_symbol] = match_lots_in_fifo(lots, [] if existing_lots is None else existing_lots)
    for company_symbol, lots in existing_unrealized_lots_map.items():
        if matching_result_map.get(company_symbol) is None:
            matching_result_map[company_symbol] = MatchingResult(realized=Realized([]), unrealized=Unrealized(lots))
    return matching_result_map
from enum import Enum, auto
import re
from typing import NamedTuple, Protocol
from datetime import date, datetime

import pandas as pd


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

def to_lots_by_company_symbol(tradings_df: pd.DataFrame) -> dict[str, list[Lot]]:
    result: dict[str, list[Lot]] = {}
    for _, row in tradings_df.iterrows():
        company_identifier, lots = _to_lot(row)
        result.setdefault(company_identifier, []).append(lots)
    return result

class RealizedLotsGroup(NamedTuple):
    sell_lot:SellLot
    buy_lots: list[BuyLot]
    def realized_gain(self):
        return sum([lot.value_in_cent if lot.action == Action.BUY else -lot.value_in_cent for lot in self.lots])



class MatchingResult(NamedTuple):
    realized_lists_groups:list[RealizedLotsGroup]
    remaining_lots:list[BuyLot]

def match_lots_in_fifo(tradings:list[Lot], existing_unrealized_lots:list[BuyLot]=[]) -> MatchingResult:
    remaining_lots: list[BuyLot] = list(existing_unrealized_lots)
    def dequeue(sell_lot: SellLot) -> RealizedLotsGroup:
        buy_lots = []
        amount_to_dequeue = sell_lot.share_amount
        while amount_to_dequeue > 0:
            head_lot = remaining_lots[0]
            if head_lot.share_amount <= amount_to_dequeue:
                amount_to_dequeue -= head_lot.share_amount
                buy_lots.append(remaining_lots.pop(0))
        return RealizedLotsGroup(sell_lot=sell_lot, buy_lots=buy_lots)
    realized_lots_list = []
    for tr in tradings:
        if tr.action() == Action.BUY:
            remaining_lots.append(tr)
        else:
            realized_lots_list.append(dequeue(tr))

    return MatchingResult(realized_lots_list, remaining_lots)
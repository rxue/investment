from enum import Enum, auto
import re
from typing import NamedTuple, Tuple, List
from datetime import date, datetime

import pandas as pd


class Action(Enum):
    BUY = auto()
    SELL = auto()

class Lot(NamedTuple):
    date: date
    action:Action
    share_amount: int
    value_in_cent: int

def _to_lot(row: pd.Series) -> tuple[str,Lot]:
    match = re.match(r"^\s*([OM]):(.+?)\s*/(\d+)", row["Viesti"])
    action = match.group(1)
    ticker = match.group(2).strip()
    quantity = int(match.group(3))
    trading_date = datetime.strptime(row["Kirjauspäivä"], "%d.%m.%Y").date()
    trade_price = abs(row["Määrä EUROA"])
    return ticker, Lot(date=trading_date,
               action=Action.BUY if action == "O" else Action.SELL,
               share_amount=quantity,
               value_in_cent=int(trade_price * 100))

def to_lots_by_company_symbol(tradings_df: pd.DataFrame) -> dict[str, list[Lot]]:
    result: dict[str, list[Lot]] = {}
    for _, row in tradings_df.iterrows():
        company_identifier, lots = _to_lot(row)
        result.setdefault(company_identifier, []).append(lots)
    return result

class RealizedLots(NamedTuple):
    lots: list[Lot]
    def realized_gain(self):
        return sum([lot.value_in_cent if lot.action == Action.BUY else -lot.value_in_cent for lot in self.lots])
class UnrealizedLots(NamedTuple):
    lots: list[Lot]
    def holding(self) -> int:
        return sum(lot.share_amount for lot in self.lots)

    def holding_cost_in_cent(self) -> int:
        return sum(lot.value_in_cent for lot in self.lots)

def fifo_lots_matching(tradings:list[Lot], existing_unrealized_lots:list[Lot]=[]) -> Tuple[List[RealizedLots],UnrealizedLots]:
    remaining_lots: list[Lot] = list(existing_unrealized_lots)
    def dequeue(buy_trading:Lot) -> RealizedLots:
        head_lot = remaining_lots[0]
        realized = [buy_trading]
        amount_to_dequeue = buy_trading.share_amount
        while amount_to_dequeue > 0:
            if buy_trading.share_amount >= head_lot.share_amount:
                amount_to_dequeue -= head_lot.share_amount
                realized.append(remaining_lots.pop(0))
        return RealizedLots(realized)
    realized_lots_list = []
    for tr in tradings:
        if tr.action == Action.BUY:
            remaining_lots.append(tr)
        else:
            realized_lots_list.append(dequeue(tr))

    return realized_lots_list, UnrealizedLots(remaining_lots)
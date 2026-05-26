from enum import Enum, auto
import re
from typing import NamedTuple
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

class Trading(NamedTuple):
    company_identifier:str  # any string identifying the company, e.g. company name in Nordea trading lots, ticker symbol in OP
    action:str
    date:date
    amount:int
    trade_price:float

def to_lots_by_company_symbol(tradings: pd.DataFrame) -> dict[str, list[Lot]]:
    def _to_trading(row: pd.Series) -> Trading:
        match = re.match(r"^\s*([OM]):(.+?)\s*/(\d+)", row["Viesti"])
        action = match.group(1)
        ticker = match.group(2).strip()
        quantity = int(match.group(3))
        trade_date = datetime.strptime(row["Kirjauspäivä"], "%d.%m.%Y").date()
        trade_price = abs(row["Määrä EUROA"])
        return Trading(
            company_identifier=ticker,
            action=action,
            date=trade_date,
            amount=quantity,
            trade_price=trade_price,
        )

    def to_lot(t: Trading) -> Lot:
        return Lot(date=t.date,
                   action=Action.BUY if t.action == "O" else Action.SELL,
                   share_amount=t.amount,
                   value_in_cent=int(t.trade_price * 100))

    result: dict[str, list[Lot]] = {}
    for _, row in tradings.iterrows():
        trading = _to_trading(row)
        result.setdefault(trading.company_identifier, []).append(to_lot(trading))
    return result

class RealizedLots(NamedTuple):
    lots: list[Lot]
    def realized_gain(self):
        return sum([lot.value_in_cent if lot.action == Action.BUY else -lot.value_in_cent for lot in self.lots])

class LotsMatchingResult(NamedTuple):
    realized_lots:list[RealizedLots]
    unrealized_lots:list[Lot]
    def unrealized_holding(self) -> int:
        sum([lot.share_amount for lot in self.unrealized_lots])
    def holding_cost_in_cent(self) -> int:
        return sum(lot.value_in_cent for lot in self.unrealized_lots)

def fifo_lots_matching(tradings:list[Lot], existing_unrealized_lots:list[Lot]=[]) -> LotsMatchingResult:
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

    return LotsMatchingResult(realized_lots_list, remaining_lots)
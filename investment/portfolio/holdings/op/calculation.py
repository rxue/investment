import re
from datetime import datetime

import pandas as pd

from investment.company import find_company_by
from investment.portfolio.lots_matching import Action, Lot, fifo_lots_matching
from investment.portfolio.holdings.models import Trading, Holding, Field
from investment.portfolio.holdings.util import extract_csv
from investment.portfolio.transaction_filters import find_all_tradings


class OPTrading(Trading):
    def to_lot(self) -> Lot:
        return Lot(
            date=self.date,
            action=Action.BUY if self.action == "O" else Action.SELL,
            share_amount=self.amount,
            value_in_cent=self.trade_price*100
            )

def _to_trading(row: pd.Series) -> Trading:
    match = re.match(r"^\s*([OM]):(.+?)\s*/(\d+)", row["Viesti"])
    action = match.group(1)
    ticker = match.group(2).strip()
    quantity = int(match.group(3))
    trade_date = datetime.strptime(row["Kirjauspäivä"], "%d.%m.%Y").date()
    trade_price = abs(float(row["Määrä EUROA"].replace(",", ".")))
    return OPTrading(
        company_identifier=ticker,
        action=action,
        date=trade_date,
        amount=quantity,
        trade_price=trade_price,
    )
def to_lots_by_company_symbol(tradings: pd.DataFrame) -> dict[str, list[Lot]]:
    result: dict[str, list[Lot]] = {}
    for _, row in tradings.iterrows():
        trading = _to_trading(row)
        result.setdefault(trading.company_identifier, []).append(trading.to_lot())
    return result

def extract_holdings_from_op_transaction_csvs(csv_directory: str, optional_fields:list[str]) -> tuple[list[Holding], list[str]]:
    transactions = extract_csv(path=csv_directory, sep=";", encoding="latin-1")
    tradings_df:pd.DataFrame = find_all_tradings(transactions)

    lots_matching_result_by_company_symbol = {
        company_symbol: fifo_lots_matching(input_lots)
        for company_symbol, input_lots in to_lots_by_company_symbol(tradings_df).items()
    }
    def to_holding(company_symbol:str,lots_matching_result:Result) -> Holding | None:
        company = find_company_by(company_symbol)
        if company is not None:
            filled_optional_fields = {}
            for field_val in optional_fields:
                if field_val.upper() == Field.COST.label.upper():
                    filled_optional_fields[Field.COST] = result.holding_cost_in_cent()/100
            return Holding(
                company=company,
                amount=sum(lot.share_amount for lot in lots_matching_result.unrealized_lots),
                optional_fields=filled_optional_fields)
        print(f"did not find company name for symbol: {company_symbol}")
        return None
    holdings = []
    missing_company_symbols = []
    for symbol, result in lots_matching_result_by_company_symbol.items():
        holding = to_holding(symbol, result)
        if holding is None:
            missing_company_symbols.append(symbol)
        else:
            holdings.append(holding)
    return holdings, missing_company_symbols

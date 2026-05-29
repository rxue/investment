import pandas as pd

from investment.company import find_company_by
from investment.portfolio.lots_matching import match_lots_in_fifo, to_lots_by_company_symbol, BuyLot
from investment.portfolio.holdings.models import Holding, Field
from investment.portfolio.transaction_filters import find_all_tradings
from investment.text_io import extract_csv


def extract_holdings_from_op_transaction_csvs(csv_directory: str, optional_fields:list[str]) -> tuple[list[Holding], list[str]]:
    transactions = extract_csv(path=csv_directory, sep=";", encoding="latin-1")
    tradings_df:pd.DataFrame = find_all_tradings(transactions)

    lots_matching_result_by_company_symbol = {
        company_symbol: match_lots_in_fifo(input_lots).remaining_lots
        for company_symbol, input_lots in to_lots_by_company_symbol(tradings_df).items()
    }
    def to_holding(company_symbol:str, unrealized_lots:list[BuyLot]) -> Holding | None:
        company = find_company_by(company_symbol)
        if company is not None:
            filled_optional_fields = {}
            for field_val in optional_fields:
                if field_val.upper() == Field.COST.label.upper():
                    filled_optional_fields[Field.COST] = unrealized_lots.holding_cost_in_cent() / 100
            return Holding(
                company=company,
                amount=sum(lot.share_amount for lot in unrealized_lots),
                optional_fields=filled_optional_fields)
        print(f"did not find company name for symbol: {company_symbol}")
        return None
    holdings = []
    missing_company_symbols = []
    for symbol, lots_matching_result in lots_matching_result_by_company_symbol.items():
        holding = to_holding(symbol, lots_matching_result)
        if holding is None:
            missing_company_symbols.append(symbol)
        else:
            holdings.append(holding)
    return holdings, missing_company_symbols

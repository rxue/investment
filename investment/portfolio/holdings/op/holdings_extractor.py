from investment.data_fetch.company_fetcher import find_company_by
from investment.data_fetch.models import QuoteFact
from investment.data_fetch.yfinance_fetcher import find_latest_quote_facts
from investment.portfolio.lots_matching import group_match_lots_in_fifo, MatchingResult
from investment.portfolio.holdings.models import Holding, CalculatedFact
from investment.text_io import extract_csv


def extract_holdings_from_op_transaction_csvs(csv_directory: str, *fact_names:str) -> tuple[list[Holding], list[str]]:
    def to_facts() -> tuple[list[QuoteFact],list[CalculatedFact]]:
        quote_facts = [QuoteFact[name] for name in fact_names if name in QuoteFact._member_names_]
        calculated_facts = [CalculatedFact[name] for name in fact_names if name in CalculatedFact._member_names_]
        return quote_facts, calculated_facts
    transactions = extract_csv(path=csv_directory, sep=";", encoding="latin-1")
    matching_result_map:dict[str,MatchingResult] = group_match_lots_in_fifo(transactions)
    def to_holding(company_symbol:str, matching_result:MatchingResult) -> Holding:
        quote_facts, calculated_facts = to_facts()
        company = find_company_by(company_symbol)
        fact_map = find_latest_quote_facts(company.yahoo_symbol, *quote_facts)
        for calculated_fact in calculated_facts:
            if calculated_fact == CalculatedFact.COST:
                fact_map[calculated_fact] = matching_result.unrealized.cost()
            elif calculated_fact == CalculatedFact.DAILY_CHANGE_PERCENTAGE:
                current_price = fact_map[QuoteFact.PRICE]
                daily_change = fact_map[QuoteFact.DAILY_CHANGE]
                daily_change_rate = daily_change/(current_price - daily_change)
                fact_map[calculated_fact] = f"{daily_change_rate*100:.2f}%"

        return Holding(company, matching_result.unrealized.position(), fact_map) if company is not None else None
    return [to_holding(_company_symbol, matching_result) for _company_symbol, matching_result in matching_result_map.items()], []

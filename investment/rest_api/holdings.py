from fastapi import APIRouter

from investment.holdings.models import HoldingsSnapshot
from investment.holdings.op.calculation import extract_holdings_from_op_transaction_csvs

router = APIRouter()


@router.get("/holdings")
def get_holdings():
    holdings, failed_companies = extract_holdings_from_op_transaction_csvs("/home/rui/Documents/investment/company_data/tiliote/extracted", [])
    holdings_snapshot, companies_failed_to_get_quote = HoldingsSnapshot.generate_snapshot(holdings)
    companies_failed_to_get_quote.extend(failed_companies)
    return holdings_snapshot


import pandas as pd

from investment.company import find_company_by
from investment.holdings.models import HoldingsSnapshot, Holding, Bank


def extract_holdings_from_excel(file_path: str) -> tuple[list[Holding], list[str]]:
    #if Path(file_path).name.startswith("nordea"):
    #    return extract_from_nordnet_csv(file_path)
    df = pd.read_excel(file_path, header=1)
    custody_rows = df[df["Type"] == "Custody"].dropna(subset=["ISIN"])
    holdings = []
    missing_companies=[]
    for _, row in custody_rows.iterrows():
        company = find_company_by(row["NAME"])
        if company is None:
            missing_companies.append(row["NAME"])
        else:
            holdings.append(Holding(company=company,amount=int(row["HOLDINGS"])))
    return holdings, missing_companies


def generate_holdings(holdings_excel_path: str) -> tuple[HoldingsSnapshot, list[str]]:
    holdings, missing_company_isins = extract_holdings_from_excel(holdings_excel_path)
    holdings_with_quote = []
    for h in holdings:
        holding_with_quote = h.with_quote()
        if holding_with_quote is None:
            missing_company_isins.append(h.company.name)
        else:
            holdings_with_quote.append(holding_with_quote)
    return HoldingsSnapshot(Bank.NORDEA, holdings_with_quote), missing_company_isins

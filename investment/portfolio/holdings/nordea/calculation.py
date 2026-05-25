
import pandas as pd

from investment.company import find_company_by
from investment.holdings.models import Holding


def extract_nordea_holdings_from_excel(file_path: str, optional_fields:list[str]) -> tuple[list[Holding], list[str]]:
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

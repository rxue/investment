from investment.portfolio.holdings.models import Holding
from investment.text_io import extract_csv


def extract_from_nordnet_csv(file_path: str) -> list[Holding]:
    df = extract_csv(file_path)
    return [
        Holding(
            company_name=row["Nimi"].strip(),
            amount=int(row["Määrä"]),
        )
        for _, row in df.iterrows()
    ]


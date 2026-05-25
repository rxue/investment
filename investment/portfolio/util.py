import pandas as pd


def make_df(*rows: pd.Series) -> pd.DataFrame:
    return pd.DataFrame(list(rows))
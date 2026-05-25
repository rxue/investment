
import pandas as pd


def find_all_tradings(df: pd.DataFrame) -> pd.DataFrame:
    """Find stock trading transactions grouped by ticker symbol.

    Args:
        df: DataFrame of all the transactions

    Returns:
        Data frame with tradings only
    """
    def is_trading(row)->bool:
        return row["Laji"] == 700 and (row["Selitys"] == "NOSTO" or row["Selitys"] == "PANO")
    return df[df.apply(is_trading, axis=1)]
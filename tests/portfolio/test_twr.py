import pandas as pd

from investment.portfolio.twr import _is_withdrawal, _is_deposit, divide_transactions_by_period
from investment.portfolio.util import make_df


def make_row(value_date:str | None = None, *, maara:str, laji:int, selitys:str, viesti:str) -> pd.Series:
    return pd.Series({"Laji": laji, "Arvopäivä": value_date, "Määrä EUROA": float(maara.replace(",", ".")), "Selitys": selitys, "Viesti": viesti})


def test_withdrawal_returns_true():
    row = make_row(laji=700, maara="-500,00", selitys="TILISIIRTO", viesti="some message")
    assert _is_withdrawal(row) is True

def test_withdrawal_internal_payment():
    row = make_row(laji=730, maara="-1,04", selitys="PALVELUMAKSU", viesti="PALVELUMAKSUT")
    assert _is_withdrawal(row) is False
    assert _is_deposit(row) is False

def test_withdrawal_positive_amount_returns_false():
    row = make_row(laji=700, maara="500,00", selitys="TILISIIRTO", viesti="some message")
    assert _is_withdrawal(row) is False

def test_withdrawal_without_message():
    row = make_row(laji=700, maara="-15,49", selitys="TILISIIRTO", viesti="")
    assert _is_withdrawal(row) is True
    assert _is_deposit(row) is False

def test_withdrawal_strips_whitespace_before_O_check():
    row = make_row(laji=700, maara="-500,00", selitys="NOSTO", viesti="  O:NOVO B /20")
    assert _is_withdrawal(row) is False

def test_deposit():
    row = make_row(laji=710, maara="7000", selitys="TILISIIRTO", viesti="SEPA MAKSU ...")
    assert _is_deposit(row) is True
    assert _is_withdrawal(row) is False

def test_deposit_dividend_payment_is_considered_as_inflow_from_external():
    row = make_row(laji=710, maara="13,07", selitys="ARVOPAPERIT", viesti="OP Säilytys Oy")
    assert _is_deposit(row) is False
    assert _is_withdrawal(row) is False

def test_construct_subperiods_one():
    df = make_df(make_row(value_date="21.7.2025", laji=710, maara="7000", selitys="TILISIIRTO", viesti="SEPA-MAKSU"),
                 make_row(value_date="21.07.2025", laji=700, maara="-899,99", selitys="TILISIIRTO", viesti=""))
    result = divide_transactions_by_period(df)
    assert len(result) == 1
    assert len(result[0]) == 2, "2 transactions are expected to be inside the same sub-period"

def test_construct_subperiods_two():
    df = make_df(make_row(value_date="21.07.2025", laji=710, maara="7000", selitys="TILISIIRTO", viesti="SEPA-MAKSU"),
                 make_row(value_date="21.07.2025", laji=700, maara="-899,99", selitys="TILISIIRTO", viesti=""),
                 make_row(value_date="31.07.2025", laji=700, maara="-50", selitys="TILISIIRTO", viesti="Loppullinen saaja:..."))
    result = divide_transactions_by_period(df)
    assert len(result) == 2
    first_df = result[0]
    assert len(first_df) == 2
    assert first_df.iloc[0]["Arvopäivä"] == "21.07.2025"
    assert first_df.iloc[1]["Arvopäivä"] == "21.07.2025"
    second_df = result[1]
    assert len(second_df) == 1
    assert second_df.iloc[0]["Arvopäivä"] == "31.07.2025"

def test_construct_subperiods_ended_by_external_payment():
    df = make_df(make_row(value_date="21.07.2025", laji=710, maara="7000", selitys="TILISIIRTO", viesti="SEPA-MAKSU"),
                 make_row(value_date="21.07.2025", laji=700, maara="-899,99", selitys="TILISIIRTO", viesti=""),
                 make_row(value_date="31.07.2025", laji=700, maara="-50", selitys="TILISIIRTO", viesti="Loppullinen saaja:..."),
                 make_row(value_date="03.08.2025", laji=700, maara="-15,49", selitys="TILISIIRTO", viesti=""))
    result = divide_transactions_by_period(df)
    assert len(result) == 3, "is expected to be divided into 3 sub-periods"
    first_df = result[0]
    assert len(first_df) == 2
    assert first_df.iloc[0]["Arvopäivä"] == "21.07.2025"
    assert first_df.iloc[1]["Arvopäivä"] == "21.07.2025"
    second_df = result[1]
    assert len(second_df) == 1
    assert second_df.iloc[0]["Arvopäivä"] == "31.07.2025"
    third_df = result[2]
    assert len(third_df) == 1
    assert third_df.iloc[0]["Arvopäivä"] == "03.08.2025"

def test_construct_subperiods_first_is_external_payment():
    df = make_df(make_row(value_date="21.07.2025", laji=710, maara="7000", selitys="TILISIIRTO", viesti="SEPA-MAKSU"),
        make_row(value_date="03.08.2025", laji=700, maara="-15,49", selitys="TILISIIRTO", viesti="ELISA"),
        make_row(value_date="01.09.2025", laji=710, maara="8000", selitys="TILISIIRTO", viesti="SEPA-MAKSU"),
        make_row(value_date="03.09.2025", laji=700, maara="-525,66", selitys="NOSTO", viesti="SEPA-MAKSU"))

    result = divide_transactions_by_period(df)
    assert len(result) == 2
    first_df = result[0]
    assert len(first_df) == 2
    assert first_df.iloc[0]["Arvopäivä"] == "21.07.2025"
    assert first_df.iloc[1]["Arvopäivä"] == "03.08.2025"
    second_df = result[1]
    assert len(second_df) == 2
    assert second_df.iloc[0]["Arvopäivä"] == "01.09.2025"
    assert second_df.iloc[1]["Arvopäivä"] == "03.09.2025"



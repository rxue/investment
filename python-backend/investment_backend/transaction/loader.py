from enum import Enum, auto

from investment.portfolio.transaction import Transaction

from investment_backend.transaction._nordnet_loader import NordnetTransactionsLoader
from investment_backend.transaction._op import transaction as _op_transaction


class Account(Enum):
    OP = auto()
    NORDNET = auto()

def load_transactions(account:Account, *csv_paths:str) -> list[Transaction]:
    if account == Account.OP:
        custom_transactions = _op_transaction.load_transactions(list(csv_paths))
    elif account == Account.NORDNET:
        loader = NordnetTransactionsLoader(csv_encoding="utf-16", delimiter="\t")
        all_transactions = []
        for csv_path in csv_paths:
            all_transactions += loader.load(csv_path)
        return all_transactions
    else:
        raise ValueError(f"Unsupported account: {account}")
    return [custom_tr.to_transaction() for custom_tr in custom_transactions]

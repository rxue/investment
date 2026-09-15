"""Command-line entry point for the investment toolkit.

Usage examples::

    investment load ./statements
    investment benchmark ./statements ^GSPC
    investment twr_cumulative_returns ./statements
"""
import argparse
from datetime import date
from enum import StrEnum
from pathlib import Path
from typing import Sequence

import matplotlib.dates as mdates
import matplotlib.pyplot as plt
import pandas as pd

from investment.benchmark.benchmark import BenchmarkResult
from investment.portfolio.transaction import Transaction
from investment.portfolio.twr.calculation import calculate_cumulate_returns
from investment.vo.value_objects import IndexSeries

from investment_backend.transaction._op.transaction import load_transactions
from investment_backend.transaction.loader import Account
from investment_backend.transaction.loader import load_transactions as load_account_transactions


class Command(StrEnum):
    LOAD = "load"
    BENCHMARK = "benchmark"
    TWR_CUMULATIVE_RETURNS = "twr_cumulative_returns"


def _build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        prog="investment", description="Investment portfolio toolkit."
    )
    subparsers = parser.add_subparsers(dest="command", required=True)

    load_parser = subparsers.add_parser(
        Command.LOAD, help="Load OP bank transactions from a directory of CSV files."
    )
    load_parser.add_argument(
        "csv_directory",
        help="Directory containing OP bank statement CSV files.",
    )

    benchmark_parser = subparsers.add_parser(
        Command.BENCHMARK,
        help="Compare the portfolio's time-weighted return against a benchmark index.",
    )
    benchmark_parser.add_argument(
        "csv_directory",
        help="Directory containing OP bank statement CSV files.",
    )
    benchmark_parser.add_argument(
        "security_id",
        help="Ticker symbol of the benchmark index/security to compare against.",
    )

    twr_cumulative_returns_parser = subparsers.add_parser(
        Command.TWR_CUMULATIVE_RETURNS,
        help="Plot the portfolio's daily time-weighted cumulative return, with month labels on the x-axis.",
    )
    twr_cumulative_returns_parser.add_argument(
        "account_type",
        choices=[account.name for account in Account],
        help="Account the CSV files belong to.",
    )
    twr_cumulative_returns_parser.add_argument(
        "csv_directory",
        help="Directory containing the account's statement CSV files.",
    )
    return parser


def _load_op_transactions(csv_directory: str) -> list[Transaction]:
    csv_paths = [str(path) for path in sorted(Path(csv_directory).glob("*.csv"))]
    op_transactions = load_transactions(csv_paths)
    return sorted((tr.to_transaction() for tr in op_transactions), key=lambda t: t.date)


def _plot(*index_series: IndexSeries) -> None:
    _, ax = plt.subplots(figsize=(16, 6))
    for series in index_series:
        pd.Series(series.value_by_date).sort_index().plot(ax=ax, label=series.label)
    ax.xaxis.set_major_locator(mdates.MonthLocator())
    ax.xaxis.set_major_formatter(mdates.DateFormatter("%Y-%m"))
    plt.setp(ax.get_xticklabels(), rotation=45, ha="right")
    ax.set_xlabel("Date")
    ax.set_ylabel("Index (base 100)")
    ax.legend()
    plt.tight_layout()
    plt.show()


def _plot_cumulative_returns(cumulative_returns: dict[date, float]) -> None:
    _, ax = plt.subplots(figsize=(16, 6))
    pd.Series(cumulative_returns).sort_index().plot(ax=ax, label="Cumulative TWR")
    ax.xaxis.set_major_locator(mdates.MonthLocator())
    ax.xaxis.set_major_formatter(mdates.DateFormatter("%Y-%m"))
    plt.setp(ax.get_xticklabels(), rotation=45, ha="right")
    ax.set_xlabel("Date")
    ax.set_ylabel("Cumulative return")
    ax.legend()
    plt.tight_layout()
    plt.show()


def main(argv: Sequence[str] | None = None) -> None:
    parser = _build_parser()
    args = parser.parse_args(argv)
    if args.command == Command.LOAD:
        for transaction in _load_op_transactions(args.csv_directory):
            print(transaction)
    elif args.command == Command.BENCHMARK:
        transactions = _load_op_transactions(args.csv_directory)
        result = BenchmarkResult.benchmark_portfolio(args.security_id, transactions)
        _plot(result.benchmark_series, result.subject_series)
    elif args.command == Command.TWR_CUMULATIVE_RETURNS:
        account = Account[args.account_type]
        csv_paths = [str(path) for path in sorted(Path(args.csv_directory).glob("*.csv"))]
        transactions = load_account_transactions(account, *csv_paths)
        cumulative_returns = calculate_cumulate_returns(transactions)
        _plot_cumulative_returns(cumulative_returns)
    else:  # pragma: no cover - guarded by argparse's `required=True`
        parser.error(f"Unknown command: {args.command}")


if __name__ == "__main__":
    main()

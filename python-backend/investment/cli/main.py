"""Command-line entry point for the investment toolkit.

Usage examples::

    investment load ./statements
"""
import argparse
from enum import StrEnum
from pathlib import Path
from typing import Sequence

from investment.op.transaction import load_transactions


class Command(StrEnum):
    LOAD = "load"


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
    return parser


def main(argv: Sequence[str] | None = None) -> None:
    parser = _build_parser()
    args = parser.parse_args(argv)
    if args.command == Command.LOAD:
        csv_paths = [str(path) for path in sorted(Path(args.csv_directory).glob("*.csv"))]
        transactions = load_transactions(csv_paths)
        for tr in transactions:
            print(tr)
    else:  # pragma: no cover - guarded by argparse's `required=True`
        parser.error(f"Unknown command: {args.command}")


if __name__ == "__main__":
    main()

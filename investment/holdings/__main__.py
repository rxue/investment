import sys
from investment.holdings.models import Bank, HoldingsSnapshot, Holding
from investment.holdings.nordea_trading_lots_extractor import extract
from investment.holdings.op.calculation import extract_holdings_from_op_transaction_csvs
from investment.holdings.nordea.calculation import extract_nordea_holdings_from_excel
from investment.holdings.return_calculation import calculate_total_return


EXTRACT_HOLDINGS = "extract_from_nordea_excel"
EXTRACT_RETURN_BREAKDOWN = "extract_return_breakdown_from_nordea_pdf"
TOTAL_RETURN = "total_return"
GENERATE_HOLDINGS_SNAPSHOT = "generate_holdings_snapshot"

COMMANDS = [EXTRACT_HOLDINGS, EXTRACT_RETURN_BREAKDOWN, TOTAL_RETURN, GENERATE_HOLDINGS_SNAPSHOT]

if len(sys.argv) < 2 or sys.argv[1] not in COMMANDS:
    print(f"Usage: python -m investment.holdings <command> [args]")
    print(f"Commands: {', '.join(COMMANDS)}")
    sys.exit(1)

command = sys.argv[1]
'''
if command == EXTRACT_HOLDINGS:
    if len(sys.argv) != 3:
        print(f"Usage: python -m investment.holdings extract_nordea_excel <excel_file>")
        sys.exit(1)
    holdings = extract_from(sys.argv[2])
    for h in holdings:
        print(h)
'''
if command == EXTRACT_RETURN_BREAKDOWN:
    if len(sys.argv) != 3:
        print(f"Usage: python -m investment.holdings {EXTRACT_RETURN_BREAKDOWN} <pdf_file>")
        sys.exit(1)
    return_breakdown = extract(sys.argv[2])
    print(f"capital gain: {return_breakdown.total_capital_gain_in_cent()/100}")
    print(f"capital loss: {return_breakdown.total_capital_loss_in_cent()/100}")
elif command == TOTAL_RETURN:
    if len(sys.argv) != 3:
        print(f"Usage: python -m investment.holdings {TOTAL_RETURN} <pdf_dir>")
        sys.exit(1)
    print(calculate_total_return(sys.argv[2]))

elif command == GENERATE_HOLDINGS_SNAPSHOT:
    def generate_and_print_snapshot(holdings:list[Holding], companies_failed_get_holding:list[str]):
        holdings_snapshot, companies_failed_to_get_quote = HoldingsSnapshot.generate_snapshot(holdings)
        companies_failed_to_get_quote.extend(companies_failed_get_holding)
        print(holdings_snapshot.to_dataframe())
        if len(companies_failed_to_get_quote) > 0:
            print("The following companies fail to get quote")
            print(companies_failed_to_get_quote)
    if len(sys.argv) < 4:
        print(f"Usage: python -m investment.holdings generate_holdings_snapshot <bank> <excel_file>")
        sys.exit(1)
    elif len(sys.argv) == 5:
        optional_fields = sys.argv[4].split(",")
    else:
        optional_fields = []
    bank = Bank[sys.argv[2].upper()]
    if bank == Bank.OP:
        csv_dir = sys.argv[3]
        generate_and_print_snapshot(*extract_holdings_from_op_transaction_csvs(csv_dir, optional_fields))
    elif bank == Bank.NORDEA:
        excel_path = sys.argv[3]
        generate_and_print_snapshot(*extract_nordea_holdings_from_excel(excel_path, optional_fields))
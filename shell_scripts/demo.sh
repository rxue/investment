#!/usr/bin/env bash
set -e

GREEN='\033[0;32m'
CYAN='\033[0;36m'
BOLD='\033[1m'
RESET='\033[0m'

ls_highlight_pdf() {
    ls | while read -r line; do
        if [[ "$line" == *.pdf ]]; then
            echo -e "${GREEN}${BOLD}$line${RESET}"
        else
            echo "$line"
        fi
    done
}

type_text() {
    local color="$1"
    local text="$2"
    local delay="${3:-0.08}"
    echo -ne "${color}"
    for ((i = 0; i < ${#text}; i++)); do
        echo -ne "${text:$i:1}"
        sleep "$delay"
    done
    echo -e "${RESET}"
}

intro() {
    type_text "${BOLD}" "=== Financial Statements Generator ==="
    echo
    type_text "${CYAN}" "This program generates financial statements and forms needed when reporting tax based on"
    type_text "${CYAN}" "bank statement CSV files from a given directory:"
    type_text "${CYAN}" "* module investment.accounting produces two documents: an Income Statement and a Balance Sheet."
    type_text "${CYAN}" "* module investment.tax_report produces two forms at the moment:"
    type_text "${CYAN}" " - List of Securities and Book Entry Shares (Form 8A)"
    type_text "${CYAN}" " - Tax Paid Abroad (in Form 70)"
    echo
    type_text "${CYAN}" "Example input data directory: ~/Documents/company_data/tiliote/extracted/"
    ls ~/Documents/company_data/tiliote/extracted/
}

generate_financial_statements() {
    type_text "${BOLD}" "--- Generate Financial Statements ---"
    echo
    type_text "${CYAN}" "command: python -m investment.accounting financial-statements-pdf --input-dir <dir> --end-date <YYYY-MM-DD> --output-dir <dir> --company <name>"
    echo
    (sleep 0.3 && echo 'python -m investment.accounting financial-statements-pdf --input-dir ~/Documents/company_data/tiliote/extracted/ --end-date 2026-02-28 --output-dir output --company "Fake Company"' | xclip -selection clipboard && xdotool key ctrl+shift+v) &
}

generate_tax_reports() {
    type_text "${BOLD}" "--- Generate Tax Reports (NOTE! this is getting closing prices from internet, thus takes time) ---"
    echo
    type_text "${CYAN}" "command: python -m investment.tax_report tax-reports-pdf --input-dir <dir> --end-date <YYYY-MM-DD> --output-dir <dir>"
    echo
    (sleep 0.3 && echo 'python -m investment.tax_report tax-reports-pdf --input-dir ~/Documents/company_data/tiliote/extracted/ --end-date 2026-02-28 --output-dir output' | xclip -selection clipboard && xdotool key ctrl+shift+v) &
}

show_sample_data() {
    type_text "${CYAN}" "I am going to display sample input data, i.e. bank statement csv file"
    libreoffice --calc ~/Documents/company_data/tiliote/extracted/Tiliote_2026-01-31_2026-02-27.csv
}

display_output() {
    local output_dir="${2:-output}"
    type_text "${CYAN}" "Output directory: $output_dir"
    ls "$output_dir"
    echo
    for f in "$output_dir"/*.pdf; do
        [ -f "$f" ] || continue
        type_text "${CYAN}" "Opening $f ..."
        evince "$f" &
        EVINCE_PID=$!
        sleep 2

        # Focus the evince window and scroll once
        xdotool search --sync --pid $EVINCE_PID windowfocus
        xdotool click --repeat 20 5

        sleep 1
        kill $EVINCE_PID 2>/dev/null
        sleep 1
    done
}

end() {
    type_text "${BOLD}" "Thanks for watching!"
    sleep 2
}

case "$1" in
    intro)
        intro
        ;;
    generate-financial-statements)
        generate_financial_statements
        ;;
    generate-tax-reports)
        generate_tax_reports
        ;;
    show-sample-data)
        show_sample_data
        ;;
    display-output)
        display_output "$@"
        ;;
    end)
        end
        ;;
    *)
        echo -e "${BOLD}Usage:${RESET} $0 {intro|generate-financial-statements|generate-tax-reports|show-sample-data|display-output|end}"
        exit 1
        ;;
esac

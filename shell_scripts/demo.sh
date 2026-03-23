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
    type_text "${BOLD}" "=== Financial Statements and Tax Forms Generator ==="
    echo
    type_text "${CYAN}" "This program generates financial statements and tax forms from bank statement CSV files."
    echo
    type_text "${CYAN}" "Two modules are available:"
    type_text "${CYAN}" "* investment.accounting -- generates financial statements only:"
    type_text "${CYAN}" "    - Income Statement"
    type_text "${CYAN}" "    - Balance Sheet"
    type_text "${CYAN}" "* investment.tax_report -- generates the following forms needed in tax report:"
    type_text "${CYAN}" "    - List of Securities and Book Entry Shares (Form 8A)"
    type_text "${CYAN}" "    - Tax Paid Abroad (Form 70)"
    echo
    type_text "${CYAN}" "Example input data directory: ~/Documents/company_data/tiliote/extracted/"
    ls ~/Documents/company_data/tiliote/extracted/
    echo
    sleep 2
    type_text "${CYAN}" "Example config.yml:"
    cat config.yml
}

generate_financial_statements() {
    type_text "${BOLD}" "--- Generate Financial Statements ---"
    echo
    type_text "${CYAN}" "command: python -m investment.accounting financial-statements-pdf --input-dir <dir> --end-date <YYYY-MM-DD> --output-dir <dir> --company <name>"
    echo
    (sleep 0.3 && echo 'python -m investment.accounting financial-statements-pdf --input-dir ~/Documents/company_data/tiliote/extracted/ --end-date 2026-02-28 --output-dir output --company "Fake Company"' | xclip -selection clipboard && xdotool key ctrl+shift+v) &
}

generate_tax_reports() {
    type_text "${BOLD}" "--- Generate Tax Reports (Income Statement, Balance Sheet, Form 8A, Form 70) ---"
    type_text "${CYAN}" "NOTE: fetches closing prices from the internet, so this takes a moment"
    echo
    type_text "${CYAN}" "command: python -m investment.tax_report pdf config.yml"
    echo
    (sleep 0.3 && echo 'python -m investment.tax_report pdf config.yml' | xclip -selection clipboard && xdotool key ctrl+shift+v) &
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

        # Focus the evince window and scroll through 2/3 of the pages
        xdotool search --sync --pid $EVINCE_PID windowfocus
        total_pages=$(pdfinfo "$f" | awk '/^Pages:/ {print $2}')
        pages_to_show=$(( (total_pages * 2 + 2) / 3 ))
        for ((p = 0; p < pages_to_show; p++)); do
            xdotool key Next
            sleep 1
        done

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

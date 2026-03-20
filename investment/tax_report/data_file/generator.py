from datetime import date, timedelta

from investment.accounting.models import Holding
from investment.market_quote.repository import find_company_by_op_symbol, find_closing_price_by_symbol
from investment.tax_report.data_file.finder import find_compulsory_fields, \
    find_code
from investment.tax_report.models import Form8ACompulsoryFields, Money


def _fill_compulsory_fields_without_accounting_period(compulsory_fields: Form8ACompulsoryFields) -> dict[str, str]:
    compulsory_fields_spec = find_compulsory_fields()
    compulsory_key_value_pair_result = {}
    for field_spec in compulsory_fields_spec:
        code = field_spec["Code"]
        allowed_value = field_spec["Allowed values"]
        if isinstance(allowed_value, str):
            compulsory_key_value_pair_result[code] = allowed_value
        elif field_spec["Description"] == "Service provider's ID code":
            compulsory_key_value_pair_result[code] = compulsory_fields.service_provider_id
        elif field_spec["Description"] == "Software that generated the file":
            compulsory_key_value_pair_result[code] = compulsory_fields.software_name
        elif field_spec["Description"] == "Identifier of the software that generated the file":
            compulsory_key_value_pair_result[code] = compulsory_fields.software_id
        elif field_spec["Description"] == "Business ID of limited company":
            compulsory_key_value_pair_result[code] = compulsory_fields.business_id
    return compulsory_key_value_pair_result

def to_form8a_raw_data(holdings:list[Holding], compulsory_fields: Form8ACompulsoryFields) -> list[tuple[str, str]]:
    pass

def to_form8a_pdf_input(holdings:list[Holding], compulsory_fields: Form8ACompulsoryFields) -> list[dict[str, str]]:
    compulsory_fields_input = _fill_compulsory_fields_without_accounting_period(compulsory_fields)
    accounting_period_code = find_code("Accounting period")
    accounting_period = compulsory_fields.accounting_period
    compulsory_fields_input[accounting_period_code + "_1"] = accounting_period.start_date_string()
    compulsory_fields_input[accounting_period_code + "_2"] = accounting_period.end_date_string()
    def backtrack_to_work_date(d: date) -> date:
        while d.weekday() > 4:
            d -= timedelta(days=1)
        return d

    def _to_money(val: float) -> Money:
        return Money.new(val)
    pdf_input_batch = []
    current_pdf_input = compulsory_fields_input.copy()
    counter = 1
    for holding in holdings:
        section = "Financial assets (Business Tax Act (EVL))"
        company = find_company_by_op_symbol(holding.symbol)
        suffix = ";" + str(counter)
        current_pdf_input[find_code(section=section,
                            description="Complete name of limited company or cooperative / Financial assets") + suffix] = company.short_name
        current_pdf_input[find_code(section=section, description="Business ID") + suffix] = "0000000-0"
        current_pdf_input[find_code(section=section, description="Quantity, pcs / Financial assets") + suffix] = str(holding.quantity)
        acquisition_cost_code = find_code(section=section,
                         description="Undepreciated acquisition cost for income tax purposes / Financial assets")
        acquisition_cost = _to_money(holding.book_value)
        current_pdf_input[acquisition_cost_code + suffix] = acquisition_cost.euros
        current_pdf_input["s" + acquisition_cost_code + suffix] = acquisition_cost.cents
        price = find_closing_price_by_symbol(company, backtrack_to_work_date(compulsory_fields.accounting_period.end))
        comparison_value_per_unit = round(price.price_in_eur() * 0.7, 2)
        comparison_value_per_unit_code = find_code(section=section, description="Comparison value of each / Financial assets")
        comparison_value_per_unit_m = _to_money(comparison_value_per_unit)
        current_pdf_input[comparison_value_per_unit_code + suffix] = comparison_value_per_unit_m.euros
        current_pdf_input["s" + comparison_value_per_unit_code + suffix] = comparison_value_per_unit_m.cents
        comparison_value_code = find_code(section=section, description="Comparison value, totals / Financial assets")
        comparison_value_m = _to_money(comparison_value_per_unit * holding.quantity)
        current_pdf_input[comparison_value_code + suffix] = comparison_value_m.euros
        current_pdf_input["s" + comparison_value_code + suffix] = comparison_value_m.cents
        if counter % 2 == 0:
            pdf_input_batch.append(current_pdf_input)
            current_pdf_input = compulsory_fields_input.copy()
        counter = counter + 1
    return pdf_input_batch

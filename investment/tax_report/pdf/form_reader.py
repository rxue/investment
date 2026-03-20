from pathlib import Path

import PyPDF2

_FORM8A_PATH = Path(__file__).parent.parent.parent.parent / "data" / "form8a_fin.pdf"


def fill_form8a(company_name: str, output_path: str, path: Path = _FORM8A_PATH) -> None:
    reader = PyPDF2.PdfReader(open(path, "rb"))
    writer = PyPDF2.PdfWriter()
    writer.append(reader)
    writer.update_page_form_field_values(writer.pages[0], {"140;1": company_name})
    fields = reader.get_fields() or {}
    for name, field in fields.items():
        print(f"{name}: {field.get('/V')}")
    with open(output_path, "wb") as f:
        writer.write(f)

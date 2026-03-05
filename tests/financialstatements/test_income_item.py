from financialstatements.income_item import DividendIncomeItem

TRANSACTION_DETAIL = (
    " OP Säilytys Oy                     SIRIUSXM HOLDINGS                  "
    "US8299331004                       Osinkotuotto                       "
    "Osinko        0,27          USD/KplOmistettu määrä             90Kpl  "
    "Tuoton määrä               24,30USDLähdevero  US15,0   %       3,65USD"
    "Veroero                     0,01USDVal.kurssi                1,1876"
)


def test_deducted_tax_per_transaction():
    assert DividendIncomeItem.deducted_tax_per_transaction(TRANSACTION_DETAIL) == 307

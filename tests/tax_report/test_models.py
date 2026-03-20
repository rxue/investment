from investment.tax_report.models import Money


def test_new():
    money = Money.new(625.7)
    assert money.euros == "625"
    assert money.cents == "70"

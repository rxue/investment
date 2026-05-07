from unittest.mock import patch

import investment.company
from investment.company import Company, find_company_by


def test_find_company_by_symbol_existing():
    with patch.object(investment.company, "companies_cache", {"PFE": Company("PFE", "Pfizer Inc")}):
        assert find_company_by("PFE") == Company("PFE", "Pfizer Inc")

def test_find_company_by_symbol_not_exist():
    with patch.object(investment.company, "companies_cache", {}):
        assert find_company_by("PFE") is None
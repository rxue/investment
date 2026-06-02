from unittest.mock import patch

import investment.data_fetch.company_fetcher
from investment.data_fetch.models import Company
from investment.data_fetch.company_fetcher import find_company_by


def test_find_company_by_symbol_existing():
    with patch.object(investment.data_fetch.company_fetcher, "companies_cache", {"PFE": Company("PFE", "Pfizer Inc")}):
        assert find_company_by("PFE") == Company("PFE", "Pfizer Inc")

def test_find_company_by_symbol_not_exist():
    with patch.object(investment.data_fetch.company_fetcher, "companies_cache", {}):
        assert find_company_by("PFE") is None
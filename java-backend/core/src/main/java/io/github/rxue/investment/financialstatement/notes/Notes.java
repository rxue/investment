package io.github.rxue.investment.financialstatement.notes;


import io.github.rxue.investmentutility.lotsmatching.OpenLots;

import java.util.List;

public class Notes {
    private final List<OpenLots> openLotsList;
    public Notes(List<OpenLots> openLotsList) {
        this.openLotsList = openLotsList;
    }
    public Investment investment() {
        return new Investment(openLotsList);
    }
}

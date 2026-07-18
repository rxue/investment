package io.github.rxue.investment.portfolio.holdings;

public enum CompulsoryField implements Field {
    COMPANY_ID(String.class),
    POSITION(Integer.class);

    private final Class<?> type;

    CompulsoryField(Class<?> type) {
        this.type = type;
    }

    @Override
    public Class<?> type() {
        return type;
    }
}

package io.github.rxue.investment.portfolio.holdings;

import io.github.rxue.investment.marketquote.Price;

import java.util.Optional;

public class Holding {
    private final String companyIdentifier;
    private final int position;
    private final Price priceInEuro;
    private Holding(Builder builder) {
        this.companyIdentifier = builder.companyIdentifier;
        this.position = builder.position;
        this.priceInEuro = builder.priceInEuro;
    }

    public String getCompanyIdentifier() {
        return companyIdentifier;
    }

    public int getPosition() {
        return position;
    }

    public Optional<Price> getPriceInEuro() {
        return Optional.of(priceInEuro);
    }

    static class Builder {
        private final String companyIdentifier;
        private final int position;
        private Price priceInEuro;

        public Builder(String companyIdentifier, int position) {
            this.companyIdentifier = companyIdentifier;
            this.position = position;
        }

        public Builder setPriceInEuro(Price priceInEuro) {
            this.priceInEuro = priceInEuro;
            return this;
        }
        public Holding build() {
            return new Holding(this);
        }
    }

}

package io.github.rxue.investment.application.jpaentity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@SequenceGenerator(name = "company id sequence", sequenceName = "company_id_seq")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company id sequence")
    private int id;
    private String yahooSymbol;
    private String opSymbol;
    private String name;

    private Company() {}

    public Company(String opSymbol, String yahooSymbol, String name) {
        this.yahooSymbol = yahooSymbol;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getOpSymbol() {
        return opSymbol;
    }

    public String getYahooSymbol() {
        return yahooSymbol;
    }

    public String getName() {
        return name;
    }
}

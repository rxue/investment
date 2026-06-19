package io.github.rxue.investment.portfolio.xirr.jpaentity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class XIRRJob {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private XIRRJobStep step;

    public Long getId() {
        return id;
    }
}

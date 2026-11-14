package io.github.rxue.investment.infrastructure;


import io.github.rxue.investment.infrastructure.persistence.WatchList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchListRepository extends JpaRepository<WatchList,Long> {
}

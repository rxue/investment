package io.github.rxue.investment.rest.application;

import io.github.rxue.investment.rest.infrastructure.WatchListRepository;
import io.github.rxue.investment.rest.infrastructure.persistence.WatchList;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
@Service
public class WatchListService {
    private final WatchListRepository watchListRepository;

    public WatchListService(WatchListRepository watchListRepository) {
        this.watchListRepository = watchListRepository;
    }

    public WatchList findWatchList(Long id) {
        return watchListRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Watch List with id " + id + " not found"));
    }
}

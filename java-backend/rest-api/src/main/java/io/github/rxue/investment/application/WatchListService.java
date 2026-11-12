package io.github.rxue.investment.application;

import io.github.rxue.investment.infrastructure.WatchListRepository;
import io.github.rxue.investment.infrastructure.persistence.WatchList;
import org.springframework.stereotype.Service;

@Service
public class WatchListService {
    private final WatchListRepository watchListRepository;

    public WatchListService(WatchListRepository watchListRepository) {
        this.watchListRepository = watchListRepository;
    }

    public WatchList findWatchList(Long id) {
        return watchListRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Watch List with id " + id + " not found"));
    }
}

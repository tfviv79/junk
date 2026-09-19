package com.example.myapp.domain;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 * StatsService
 */
@Service
public class StatsServiceImpl implements StatsService {
    private StatsRepository repo;
    public StatsServiceImpl(StatsRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Raw> findRaw(int limit) {
        return repo.findRaws(limit);
    }
}


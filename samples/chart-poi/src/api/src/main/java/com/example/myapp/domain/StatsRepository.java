package com.example.myapp.domain;

import java.util.List;

/**
 * StatsRepository
 */
public interface StatsRepository {
    List<Raw> findRaws(int limit);
    List<ReHistogram> findReHistograms();
}


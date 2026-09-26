package com.example.myapp.domain;

import java.util.List;

/**
 * StatsService
 */
public interface StatsService {
    List<Raw> findRaws(int limit);
    List<ReHistogram> findReHistograms();
}


package com.example.myapp.domain;

import java.util.List;
import java.util.Map;

/**
 * StatsService
 */
public interface StatsService {
    List<Raw> findRaws(int limit);
    List<ReHistogram> findReHistograms();
    Map<String, List<ReHistogramWithRank>> findReHistogramsWithRank(int rank);
}


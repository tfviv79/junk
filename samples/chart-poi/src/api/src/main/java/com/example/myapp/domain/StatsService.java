package com.example.myapp.domain;

import java.util.List;

/**
 * StatsService
 */
public interface StatsService {
    List<Raw> findRaw(int limit);
}


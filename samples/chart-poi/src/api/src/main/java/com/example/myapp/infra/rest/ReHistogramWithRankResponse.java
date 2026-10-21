package com.example.myapp.infra.rest;

import java.util.List;
import java.util.Map;

import com.example.myapp.domain.ReHistogramWithRank;

/**
 * ReHistogramWithRankResponse
 */
public record ReHistogramWithRankResponse(Map<String, List<ReHistogramWithRank>> raws) {

}

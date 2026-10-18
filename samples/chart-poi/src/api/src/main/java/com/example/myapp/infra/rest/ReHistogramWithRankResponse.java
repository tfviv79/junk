package com.example.myapp.infra.rest;

import java.util.List;

import com.example.myapp.domain.ReHistogramWithRank;

/**
 * ReHistogramWithRankResponse
 */
public record ReHistogramWithRankResponse(List<ReHistogramWithRank> raws) {

}

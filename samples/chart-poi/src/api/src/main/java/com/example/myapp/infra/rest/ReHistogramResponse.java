package com.example.myapp.infra.rest;

import java.util.List;

import com.example.myapp.domain.ReHistogram;

public record ReHistogramResponse(List<ReHistogram> reHistograms) {

    public ReHistogramResponse(List<ReHistogram> reHistograms) {
        this.reHistograms = reHistograms;
    }
}

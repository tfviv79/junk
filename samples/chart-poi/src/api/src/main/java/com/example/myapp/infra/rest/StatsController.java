package com.example.myapp.infra.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.myapp.domain.Raw;
import com.example.myapp.domain.ReHistogram;
import com.example.myapp.domain.ReHistogramWithRank;
import com.example.myapp.domain.StatsService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class StatsController {
    private final StatsService service;

    public StatsController(StatsService service) {
        this.service = service;
    }

    @GetMapping("/raws")
    public ResponseEntity<RawResponse> raws() {
        List<Raw> raws = service.findRaws(10);

        return ResponseEntity.ok().body(new RawResponse(raws));
    }

    @GetMapping("/re_histogram")
    public ResponseEntity<ReHistogramResponse> hist() throws InterruptedException {
        List<ReHistogram> reHistograms = service.findReHistograms();
        Thread.sleep(1000);

        return ResponseEntity.ok().body(new ReHistogramResponse(reHistograms));
    }

    @GetMapping("/re_histogram/{rank}")
    public ResponseEntity<ReHistogramWithRankResponse> histWithRank(@PathVariable("rank") int rank) throws InterruptedException {
        final List<ReHistogramWithRank> reHistograms = service.findReHistogramsWithRank(rank);
        Thread.sleep(1000);
        return ResponseEntity.ok().body(new ReHistogramWithRankResponse(reHistograms));
    }
}

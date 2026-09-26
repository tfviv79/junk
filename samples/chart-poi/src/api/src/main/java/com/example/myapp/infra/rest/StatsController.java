package com.example.myapp.infra.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myapp.domain.Raw;
import com.example.myapp.domain.ReHistogram;
import com.example.myapp.domain.StatsService;

// @CrossOrigin
@RestController
public class StatsController {
    private final StatsService service;

    public StatsController(StatsService service) {
        this.service = service;
    }

    @GetMapping("/api/raws")
    public ResponseEntity<RawResponse> raws() {
        List<Raw> raws = service.findRaws(10);

        return ResponseEntity.ok().body(new RawResponse(raws));
    }

    @GetMapping("/api/re_histogram")
    public ResponseEntity<ReHistogramResponse> hist() {
        List<ReHistogram> reHistograms = service.findReHistograms();

        return ResponseEntity.ok().body(new ReHistogramResponse(reHistograms));
    }
}

package com.example.myapp.infra.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myapp.domain.Raw;
import com.example.myapp.domain.StatsService;

@RestController
public class StatsController {
    private final StatsService service;

    public StatsController(StatsService service) {
        this.service = service;
    }

    @GetMapping("/api/raw")
    public ResponseEntity<RawResponse> raw() {
        List<Raw> raws = service.findRaw(10);

        return ResponseEntity.ok().body(new RawResponse(raws));
    }
}

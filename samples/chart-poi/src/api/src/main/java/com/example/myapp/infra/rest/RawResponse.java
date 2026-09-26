package com.example.myapp.infra.rest;

import java.util.List;

import com.example.myapp.domain.Raw;

public record RawResponse(List<Raw> raws) {
    public RawResponse(List<Raw> raws) {
        this.raws = raws;
    }
}

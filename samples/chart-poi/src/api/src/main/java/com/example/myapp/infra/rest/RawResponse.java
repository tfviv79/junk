package com.example.myapp.infra.rest;

import java.util.List;

import com.example.myapp.domain.Raw;

import lombok.Data;

@Data
public class RawResponse {
    private List<Raw> raws;

	public RawResponse(List<Raw> raws) {
        this.raws = raws;
	}
}

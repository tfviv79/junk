package com.example.myapp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Raw
 */
@Data
@AllArgsConstructor
public class Raw {
    private int treat;
    private int age;
    private int education;
    private boolean black;
    private boolean hispanic;
    private boolean married;
    private boolean nodegree;
    private double re74;
    private double re75;
    private double re78;
}


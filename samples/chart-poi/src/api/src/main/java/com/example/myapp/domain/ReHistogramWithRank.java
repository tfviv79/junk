package com.example.myapp.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ReHistgram
 */
public record ReHistogramWithRank(
        int id, int treat, int age, int education,
        boolean black, boolean hispanic, boolean married,
        boolean nodegree, double re74, double re75, double re78
    ) {

    public static ReHistogramWithRank from(ResultSet rs) throws SQLException {
        return new ReHistogramWithRank(
            rs.getInt("id")
            , rs.getInt("treat")
            , rs.getInt("age")
            , rs.getInt("education")
            , rs.getBoolean("black")
            , rs.getBoolean("hispanic")
            , rs.getBoolean("married")
            , rs.getBoolean("nodegree")
            , rs.getDouble("re74")
            , rs.getDouble("re75")
            , rs.getDouble("re78")
        );
    }
}


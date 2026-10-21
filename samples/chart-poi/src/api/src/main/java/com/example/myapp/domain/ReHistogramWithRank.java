package com.example.myapp.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ReHistgram
 */
public record ReHistogramWithRank(
        String name, int key, int count
    ) {

    public static ReHistogramWithRank from(ResultSet rs) throws SQLException {
        return new ReHistogramWithRank(
            rs.getString("name")
            , rs.getInt("key")
            , rs.getInt("cnt")
        );
    }
}


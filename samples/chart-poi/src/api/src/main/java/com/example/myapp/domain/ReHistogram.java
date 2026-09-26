package com.example.myapp.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ReHistgram
 */
public record ReHistogram(int reRank, int count) {

    public static ReHistogram from(ResultSet rs) throws SQLException {
        return new ReHistogram(
            rs.getInt("re_rank")
            , rs.getInt("count")
        );
    }
}


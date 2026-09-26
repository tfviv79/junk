package com.example.myapp.infra.db;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myapp.domain.Raw;
import com.example.myapp.domain.ReHistogram;
import com.example.myapp.domain.StatsRepository;

@Repository
public class StatsRepositoryImpl implements StatsRepository {

    private final JdbcTemplate jdbc;

    public StatsRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

	@Override
	public List<Raw> findRaws(int limit) {
        return jdbc.query("""
        SELECT 
            id
            , treat
            , age
            , education
            , black
            , hispanic
            , married
            , nodegree
            , re74
            , re75
            , re78
        FROM cps_controls
        LIMIT ?
        """, (rs, rowNum) -> new Raw(
            rs.getInt("treat")
            , rs.getInt("age")
            , rs.getInt("education")
            , rs.getBoolean("black")
            , rs.getBoolean("hispanic")
            , rs.getBoolean("married")
            , rs.getBoolean("nodegree")
            , rs.getDouble("re74")
            , rs.getDouble("re75")
            , rs.getDouble("re78")
        ),
        limit
        );
	}

    public List<ReHistogram> findReHistograms() {
        return jdbc.query("""
            select re_rank, count(id) from re1 group by re_rank order by re_rank;
            """, (rs, rowNum) -> ReHistogram.from(rs));
    }
}
